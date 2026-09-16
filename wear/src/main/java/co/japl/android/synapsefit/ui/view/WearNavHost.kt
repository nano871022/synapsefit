package co.japl.android.synapsefit.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import co.japl.android.synapsefit.WearDependencyProvider
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.navigation.WearRoutes
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import co.japl.android.synapsefit.ui.viewmodel.WearDaySelectionViewModel
import co.japl.android.synapsefit.ui.viewmodel.WearPostWorkoutSummaryViewModel

@Suppress("LongMethod")
@Composable
fun WearNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    val context = LocalContext.current.applicationContext
    WearDependencyProvider.initialize(context)

    LaunchedEffect(Unit) {
        WearDependencyProvider.requestActivePlanFromPhone(context)
        WearDependencyProvider.wearStateMirrorPort.sendEvent(co.japl.android.synapsefit.core.domain.model.LiveSyncEvent.PingSession("WEAR"))
    }

    val daySelectionViewModel: WearDaySelectionViewModel =
        viewModel(
            factory =
                viewModelFactory {
                    initializer {
                        WearDaySelectionViewModel(WearDependencyProvider.getTodayRoutineUseCase)
                    }
                },
        )

    val activeWorkoutViewModel: WearActiveWorkoutViewModel =
        viewModel(
            factory =
                viewModelFactory {
                    initializer {
                        WearActiveWorkoutViewModel(
                            sensorPort = WearDependencyProvider.wearSensorPort,
                            syncPort = WearDependencyProvider.wearSyncPort,
                            workoutPlanRepositoryPort = WearDependencyProvider.workoutPlanRepository,
                            wearStateMirrorPort = WearDependencyProvider.wearStateMirrorPort,
                        )
                    }
                },
        )

    val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

    LaunchedEffect(activeUiState.isSessionStarted, activeUiState.isLiveSyncActive) {
        if (activeUiState.isSessionStarted && activeUiState.isLiveSyncActive) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute == WearRoutes.DAY_SELECTION || currentRoute == WearRoutes.PRE_WORKOUT) {
                val planId = activeUiState.activePlanTitle.ifBlank { "active_plan" }
                val day = activeUiState.currentDay
                navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                    popUpTo(WearRoutes.DAY_SELECTION)
                }
            }
        }
    }

    val postWorkoutSummaryViewModel: WearPostWorkoutSummaryViewModel =
        viewModel(
            factory =
                viewModelFactory {
                    initializer {
                        WearPostWorkoutSummaryViewModel(
                            getGroupedWorkoutHistoryUseCase = WearDependencyProvider.getGroupedWorkoutHistoryUseCase,
                            syncPort = WearDependencyProvider.wearSyncPort,
                        )
                    }
                },
        )

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = WearRoutes.DAY_SELECTION,
        modifier = modifier,
    ) {
        composable(WearRoutes.DAY_SELECTION) {
            val daySelectionState by daySelectionViewModel.uiState.collectAsState()
            val localContext = LocalContext.current

            WearDaySelectionScreen(
                sessions = daySelectionState.sessions,
                onSelectSession = { planId, day ->
                    navController.navigate(WearRoutes.preWorkout(planId, day))
                },
                checkingUpdate = daySelectionState.checkingUpdate,
                updateMessageResId = daySelectionState.updateMessageResId,
                isUpdateAvailable = daySelectionState.isUpdateAvailable,
                onCheckUpdate = { daySelectionViewModel.checkForUpdates(localContext) },
                onPerformUpdate = {
                    val activity = localContext as? android.app.Activity
                    if (activity != null) {
                        daySelectionViewModel.performImmediateUpdate(activity)
                    }
                },
            )
        }

        composable(
            route = WearRoutes.PRE_WORKOUT,
            arguments =
                listOf(
                    navArgument(WearRoutes.ARG_PLAN_ID) { type = NavType.StringType },
                    navArgument(WearRoutes.ARG_DAY) {
                        type = NavType.IntType
                        defaultValue = 1
                    },
                ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(WearRoutes.ARG_PLAN_ID) ?: ""
            val day = backStackEntry.arguments?.getInt(WearRoutes.ARG_DAY) ?: 1

            LaunchedEffect(planId, day) {
                activeWorkoutViewModel.loadPlanData(planId, day)
            }

            val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

            WearPreWorkoutSelectionHubScreen(
                planTitle = activeUiState.activePlanTitle,
                currentDay = day,
                exercises = activeUiState.availableExercises,
                onSelectExercise = { exercise, index ->
                    activeWorkoutViewModel.selectExercise(exercise, index)
                    navController.navigate(WearRoutes.activeWorkout(planId, day))
                },
                onStartSession = {
                    activeWorkoutViewModel.startSession()
                    navController.navigate(WearRoutes.activeWorkout(planId, day))
                },
            )
        }

        composable(
            route = WearRoutes.ACTIVE_WORKOUT,
            arguments =
                listOf(
                    navArgument(WearRoutes.ARG_PLAN_ID) { type = NavType.StringType },
                    navArgument(WearRoutes.ARG_DAY) {
                        type = NavType.IntType
                        defaultValue = 1
                    },
                ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(WearRoutes.ARG_PLAN_ID) ?: ""
            val day = backStackEntry.arguments?.getInt(WearRoutes.ARG_DAY) ?: 1

            val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

            WearActiveWorkoutScreen(
                uiState = activeUiState,
                onIncrementReps = { activeWorkoutViewModel.incrementReps() },
                onDecrementReps = { activeWorkoutViewModel.decrementReps() },
                onCompleteSet = {
                    activeWorkoutViewModel.completeSet()
                    if (activeWorkoutViewModel.trainingStepState.value is TrainingStepState.Cooldown) {
                        navController.navigate(WearRoutes.cooldown(planId, day))
                    }
                },
                onStartNextExercise = { activeWorkoutViewModel.startNextExercise() },
                onTogglePause = { activeWorkoutViewModel.togglePauseResume() },
                onNextExercise = { activeWorkoutViewModel.navigateToNextExercise() },
                onPreviousExercise = { activeWorkoutViewModel.navigateToPreviousExercise() },
            )
        }

        composable(
            route = WearRoutes.COOLDOWN,
            arguments =
                listOf(
                    navArgument(WearRoutes.ARG_PLAN_ID) { type = NavType.StringType },
                    navArgument(WearRoutes.ARG_DAY) {
                        type = NavType.IntType
                        defaultValue = 1
                    },
                ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(WearRoutes.ARG_PLAN_ID) ?: ""
            val day = backStackEntry.arguments?.getInt(WearRoutes.ARG_DAY) ?: 1

            val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

            WearCooldownTransitionScreen(
                trainingStepState = activeUiState.trainingStepState,
                exerciseSessions = activeUiState.exerciseSessions,
                heartRateBpm = activeUiState.currentHeartRateBpm,
                onAddExtraTime = { activeWorkoutViewModel.addExtraCooldownTime() },
                onSkipRest = {
                    activeWorkoutViewModel.skipCooldown()
                    navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                        popUpTo(WearRoutes.ACTIVE_WORKOUT) { inclusive = true }
                    }
                },
                onStartNextExercise = {
                    activeWorkoutViewModel.startNextExercise()
                    navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                        popUpTo(WearRoutes.ACTIVE_WORKOUT) { inclusive = true }
                    }
                },
                onSelectExercise = { exerciseSession, index ->
                    val exercise =
                        activeUiState.availableExercises.firstOrNull {
                            it.id == exerciseSession.exerciseId
                        }
                    if (exercise != null) {
                        activeWorkoutViewModel.selectExercise(exercise, index)
                        navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                            popUpTo(WearRoutes.ACTIVE_WORKOUT) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(
            route = WearRoutes.POST_WORKOUT_SUMMARY,
            arguments =
                listOf(
                    navArgument(WearRoutes.ARG_PLAN_ID) { type = NavType.StringType },
                    navArgument(WearRoutes.ARG_DAY) {
                        type = NavType.IntType
                        defaultValue = 1
                    },
                ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(WearRoutes.ARG_PLAN_ID) ?: ""
            val day = backStackEntry.arguments?.getInt(WearRoutes.ARG_DAY) ?: 1

            LaunchedEffect(planId, day) {
                postWorkoutSummaryViewModel.loadSummaryForPlanAndDay(planId, day)
            }

            val summaryUiState by postWorkoutSummaryViewModel.uiState.collectAsState()

            WearPostWorkoutSummaryScreen(
                uiState = summaryUiState,
                onFinish = {
                    activeWorkoutViewModel.resetSessionMemory()
                    navController.navigate(WearRoutes.DAY_SELECTION) {
                        popUpTo(WearRoutes.DAY_SELECTION) { inclusive = true }
                    }
                },
            )
        }
    }
}
