package co.japl.android.synapsefit.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.WEAR_OS_SMALL_ROUND
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.japl.android.synapsefit.WearDependencyProvider
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.navigation.WearRoutes
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import co.japl.android.synapsefit.ui.viewmodel.WearDaySelectionViewModel
import co.japl.android.synapsefit.ui.viewmodel.WearPostWorkoutSummaryViewModel

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun WearNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    val context = LocalContext.current.applicationContext
    WearDependencyProvider.initialize(context)

    LaunchedEffect(Unit) {
        WearDependencyProvider.requestActivePlanFromPhone(context)
        val pingEvent = LiveSyncEvent.PingSession("WEAR")
        WearDependencyProvider.wearStateMirrorPort.sendEvent(pingEvent)
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

    LaunchedEffect(
        activeUiState.isSessionStarted,
        activeUiState.activePlanDayId,
        activeUiState.sessionStartTimestamp,
        activeUiState.activeExerciseId,
        activeUiState.exerciseStartTimestamp,
    ) {
        if (activeUiState.isSessionStarted) {
            daySelectionViewModel.updateActiveSessionState(
                activePlanDayId = activeUiState.activePlanDayId ?: activeUiState.currentDay,
                sessionStartTimestamp = activeUiState.sessionStartTimestamp,
                activeExerciseId = activeUiState.activeExerciseId,
                exerciseStartTimestamp = activeUiState.exerciseStartTimestamp,
            )
        } else {
            daySelectionViewModel.updateActiveSessionState(null, null, null, null)
        }
    }

    val postWorkoutSummaryViewModel: WearPostWorkoutSummaryViewModel =
        viewModel(
            factory =
                viewModelFactory {
                    initializer {
                        WearPostWorkoutSummaryViewModel(
                            getGroupedWorkoutHistoryUseCase =
                                WearDependencyProvider.getGroupedWorkoutHistoryUseCase,
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
            DaySelectionDestination(daySelectionViewModel, navController)
        }

        composable(
            route = WearRoutes.PRE_WORKOUT,
            arguments = standardNavArguments(),
        ) { backStackEntry ->
            PreWorkoutDestination(backStackEntry, activeWorkoutViewModel, navController)
        }

        composable(
            route = WearRoutes.ACTIVE_WORKOUT,
            arguments = standardNavArguments(),
        ) { backStackEntry ->
            ActiveWorkoutDestination(backStackEntry, activeWorkoutViewModel, navController)
        }

        composable(
            route = WearRoutes.COOLDOWN,
            arguments = standardNavArguments(),
        ) { backStackEntry ->
            CooldownDestination(backStackEntry, activeWorkoutViewModel, navController)
        }

        composable(
            route = WearRoutes.POST_WORKOUT_SUMMARY,
            arguments = standardNavArguments(),
        ) { backStackEntry ->
            PostWorkoutSummaryDestination(
                backStackEntry,
                postWorkoutSummaryViewModel,
                activeWorkoutViewModel,
                navController,
            )
        }
    }
}

private fun standardNavArguments() =
    listOf(
        navArgument(WearRoutes.ARG_PLAN_ID) { type = NavType.StringType },
        navArgument(WearRoutes.ARG_DAY) {
            type = NavType.IntType
            defaultValue = 1
        },
    )

@Composable
private fun DaySelectionDestination(
    viewModel: WearDaySelectionViewModel,
    navController: NavHostController,
) {
    val daySelectionState by viewModel.uiState.collectAsState()
    val localContext = LocalContext.current

    WearDaySelectionScreen(
        sessions = daySelectionState.sessions,
        activePlanDayId = daySelectionState.activePlanDayId,
        sessionStartTimestamp = daySelectionState.sessionStartTimestamp,
        onSelectSession = { planId, day ->
            navController.navigate(WearRoutes.preWorkout(planId, day))
        },
        checkingUpdate = daySelectionState.checkingUpdate,
        updateMessageResId = daySelectionState.updateMessageResId,
        isUpdateAvailable = daySelectionState.isUpdateAvailable,
        onCheckUpdate = { viewModel.checkForUpdates(localContext) },
        onPerformUpdate = {
            val activity = localContext as? android.app.Activity
            if (activity != null) {
                viewModel.performImmediateUpdate(activity)
            }
        },
    )
}

@Composable
private fun PreWorkoutDestination(
    backStackEntry: NavBackStackEntry,
    activeWorkoutViewModel: WearActiveWorkoutViewModel,
    navController: NavHostController,
) {
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
        activeExerciseId = activeUiState.activeExerciseId,
        exerciseStartTimestamp = activeUiState.exerciseStartTimestamp,
        onSelectExercise = { exercise, index ->
            activeWorkoutViewModel.selectExercise(exercise, index)
            val stepState = activeWorkoutViewModel.trainingStepState.value
            if (stepState is co.japl.android.synapsefit.core.domain.model.TrainingStepState.Cooldown) {
                navController.navigate(WearRoutes.cooldown(planId, day))
            } else {
                navController.navigate(WearRoutes.activeWorkout(planId, day))
            }
        },
        onStartSession = {
            activeWorkoutViewModel.startSession()
            val stepState = activeWorkoutViewModel.trainingStepState.value
            if (stepState is co.japl.android.synapsefit.core.domain.model.TrainingStepState.Cooldown) {
                navController.navigate(WearRoutes.cooldown(planId, day))
            } else {
                navController.navigate(WearRoutes.activeWorkout(planId, day))
            }
        },
    )
}

@Composable
private fun ActiveWorkoutDestination(
    backStackEntry: NavBackStackEntry,
    activeWorkoutViewModel: WearActiveWorkoutViewModel,
    navController: NavHostController,
) {
    val planId = backStackEntry.arguments?.getString(WearRoutes.ARG_PLAN_ID) ?: ""
    val day = backStackEntry.arguments?.getInt(WearRoutes.ARG_DAY) ?: 1

    val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

    WearActiveWorkoutScreen(
        uiState = activeUiState,
        onIncrementReps = { activeWorkoutViewModel.incrementReps() },
        onDecrementReps = { activeWorkoutViewModel.decrementReps() },
        onIncrementWgt = { activeWorkoutViewModel.incrementWgt() },
        onDecrementWgt = { activeWorkoutViewModel.decrementWgt() },
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

@Composable
private fun CooldownDestination(
    backStackEntry: NavBackStackEntry,
    activeWorkoutViewModel: WearActiveWorkoutViewModel,
    navController: NavHostController,
) {
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

@Composable
private fun PostWorkoutSummaryDestination(
    backStackEntry: NavBackStackEntry,
    postWorkoutSummaryViewModel: WearPostWorkoutSummaryViewModel,
    activeWorkoutViewModel: WearActiveWorkoutViewModel,
    navController: NavHostController,
) {
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

@Preview(device = WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearNavHostPreview() {
    MaterialThemeComposeUI {
        WearNavHost()
    }
}
