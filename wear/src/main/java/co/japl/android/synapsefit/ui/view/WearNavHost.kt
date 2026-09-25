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
import co.japl.android.synapsefit.ui.viewmodel.WearSyncViewModel

@Suppress("LongMethod", "CyclomaticComplexMethod", "LongParameterList")
@Composable
fun WearNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
    syncViewModel: WearSyncViewModel =
        viewModel {
            WearSyncViewModel(
                performWearSyncUseCase = WearDependencyProvider.performWearSyncUseCase,
                syncPort = WearDependencyProvider.wearSyncPort,
            )
        },
    daySelectionViewModel: WearDaySelectionViewModel =
        viewModel {
            WearDaySelectionViewModel(
                getTodayRoutineUseCase = WearDependencyProvider.getTodayRoutineUseCase,
                getActiveWorkoutSessionUseCase = WearDependencyProvider.getActiveWorkoutSessionUseCase,
            )
        },
    activeWorkoutViewModel: WearActiveWorkoutViewModel =
        viewModel {
            WearActiveWorkoutViewModel(
                sensorPort = WearDependencyProvider.wearSensorPort,
                syncPort = WearDependencyProvider.wearSyncPort,
                workoutPlanRepositoryPort = WearDependencyProvider.workoutPlanRepository,
                wearStateMirrorPort = WearDependencyProvider.wearStateMirrorPort,
                activeSessionRepositoryPort = WearDependencyProvider.activeSessionRepository,
            )
        },
    postWorkoutSummaryViewModel: WearPostWorkoutSummaryViewModel =
        viewModel {
            WearPostWorkoutSummaryViewModel(
                getGroupedWorkoutHistoryUseCase = WearDependencyProvider.getGroupedWorkoutHistoryUseCase,
                syncPort = WearDependencyProvider.wearSyncPort,
            )
        },
) {
    val localContext = LocalContext.current
    val mirrorPort = WearDependencyProvider.wearStateMirrorPort

    LaunchedEffect(Unit) {
        WearDependencyProvider.requestActivePlanFromPhone(localContext)
    }

    LaunchedEffect(Unit) {
        mirrorPort.liveSyncEvents.collect { event ->
            if (event is LiveSyncEvent.StartSession) {
                activeWorkoutViewModel.loadPlanData(event.planId, event.day)
                navController.navigate(WearRoutes.activeWorkout(event.planId, event.day))
            } else if (event is LiveSyncEvent.SelectExercise) {
                val state = activeWorkoutViewModel.uiState.value
                val exercise =
                    state.availableExercises.firstOrNull { it.id == event.exerciseId }
                if (exercise != null) {
                    val index = state.availableExercises.indexOf(exercise)
                    activeWorkoutViewModel.selectExercise(exercise, index)
                    navController.navigate(
                        WearRoutes.activeWorkout(
                            state.activePlanTitle,
                            state.currentDay,
                        ),
                    )
                }
            } else if (event is LiveSyncEvent.CompleteSet) {
                activeWorkoutViewModel.completeSet()
            } else if (event is LiveSyncEvent.FinishSession) {
                activeWorkoutViewModel.finishSession()
                navController.navigate(WearRoutes.sync(isPostWorkout = true)) {
                    popUpTo(WearRoutes.DAY_SELECTION)
                }
            }
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = WearRoutes.SYNC,
        modifier = modifier,
    ) {
        composable(
            route = WearRoutes.SYNC,
            arguments =
                listOf(
                    navArgument(WearRoutes.ARG_IS_POST_WORKOUT) {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) { backStackEntry ->
            val isPostWorkout = backStackEntry.arguments?.getBoolean(WearRoutes.ARG_IS_POST_WORKOUT) ?: false
            SyncDestination(isPostWorkout, syncViewModel, activeWorkoutViewModel, navController)
        }

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
private fun SyncDestination(
    isPostWorkout: Boolean,
    syncViewModel: WearSyncViewModel,
    activeWorkoutViewModel: WearActiveWorkoutViewModel,
    navController: NavHostController,
) {
    LaunchedEffect(isPostWorkout) {
        syncViewModel.startSync(isPostWorkout)
    }

    val syncUiState by syncViewModel.uiState.collectAsState()

    LaunchedEffect(syncUiState.isFinished) {
        if (syncUiState.isFinished) {
            if (syncUiState.activeSessionDetected && !syncUiState.planId.isNullOrBlank()) {
                val planId = syncUiState.planId ?: ""
                val day = syncUiState.day ?: 1
                activeWorkoutViewModel.loadPlanData(planId, day)
                navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                    popUpTo(WearRoutes.SYNC) { inclusive = true }
                }
            } else if (isPostWorkout) {
                val state = activeWorkoutViewModel.uiState.value
                val planId = state.activePlanId.orEmpty().ifEmpty { "default" }
                val day = state.currentDay
                navController.navigate(WearRoutes.postWorkoutSummary(planId, day)) {
                    popUpTo(WearRoutes.SYNC) { inclusive = true }
                }
            } else {
                navController.navigate(WearRoutes.DAY_SELECTION) {
                    popUpTo(WearRoutes.SYNC) { inclusive = true }
                }
            }
        }
    }

    WearSyncScreen(
        uiState = syncUiState,
        onCancel = { syncViewModel.cancelSync() },
    )
}

@Composable
private fun DaySelectionDestination(
    viewModel: WearDaySelectionViewModel,
    navController: NavHostController,
) {
    val daySelectionState by viewModel.uiState.collectAsState()
    val localContext = LocalContext.current

    WearDaySelectionScreen(
        sessions = daySelectionState.sessions,
        activePlanId = daySelectionState.activePlanId,
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

    val isMatchingActiveSession =
        activeUiState.isSessionStarted &&
            activeUiState.activePlanDayId == day &&
            (activeUiState.activePlanId.isNullOrBlank() || activeUiState.activePlanId == planId)

    val activeExId = if (isMatchingActiveSession) activeUiState.activeExerciseId else null
    val activeExStart = if (isMatchingActiveSession) activeUiState.exerciseStartTimestamp else null

    WearPreWorkoutSelectionHubScreen(
        planTitle = activeUiState.activePlanTitle,
        currentDay = day,
        exercises = activeUiState.availableExercises,
        activeExerciseId = activeExId,
        exerciseStartTimestamp = activeExStart,
        onSelectExercise = { exercise, index ->
            activeWorkoutViewModel.selectExercise(exercise, index)
            val stepState = activeWorkoutViewModel.trainingStepState.value
            if (stepState is TrainingStepState.Cooldown) {
                navController.navigate(WearRoutes.cooldown(planId, day))
            } else {
                navController.navigate(WearRoutes.activeWorkout(planId, day))
            }
        },
        onStartSession = {
            activeWorkoutViewModel.startSession()
            val stepState = activeWorkoutViewModel.trainingStepState.value
            if (stepState is TrainingStepState.Cooldown) {
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

    LaunchedEffect(planId, day) {
        if (planId.isNotBlank()) {
            activeWorkoutViewModel.loadPlanData(planId, day)
        }
    }

    val activeUiState by activeWorkoutViewModel.uiState.collectAsState()

    WearActiveWorkoutScreen(
        uiState = activeUiState,
        onIncrementReps = { activeWorkoutViewModel.incrementReps() },
        onDecrementReps = { activeWorkoutViewModel.decrementReps() },
        onIncrementWgt = { activeWorkoutViewModel.incrementWgt() },
        onDecrementWgt = { activeWorkoutViewModel.decrementWgt() },
        onSelectFocus = { focus -> activeWorkoutViewModel.setFocusedInput(focus) },
        onRotaryScroll = { delta -> activeWorkoutViewModel.handleRotaryScroll(delta) },
        onHardwareKey = { keyCode -> activeWorkoutViewModel.handleHardwareKey(keyCode) },
        onOpenNumericKeypad = { activeWorkoutViewModel.openNumericKeypad() },
        onCloseNumericKeypad = { activeWorkoutViewModel.closeNumericKeypad() },
        onDirectValueEntered = { valVal -> activeWorkoutViewModel.setFocusedValueDirect(valVal) },
        onCompleteSet = {
            activeWorkoutViewModel.completeSet()
            val state = activeWorkoutViewModel.uiState.value
            if (state.isRoutineCompleted) {
                navController.navigate(WearRoutes.sync(isPostWorkout = true)) {
                    popUpTo(WearRoutes.DAY_SELECTION)
                }
            } else if (activeWorkoutViewModel.trainingStepState.value is TrainingStepState.Cooldown) {
                navController.navigate(WearRoutes.cooldown(planId, day))
            }
        },
        onStartNextExercise = { activeWorkoutViewModel.startNextExercise() },
        onTogglePause = { activeWorkoutViewModel.togglePauseResume() },
        onFinishSession = {
            activeWorkoutViewModel.finishSession()
            navController.navigate(WearRoutes.sync(isPostWorkout = true)) {
                popUpTo(WearRoutes.DAY_SELECTION)
            }
        },
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

    LaunchedEffect(activeUiState.trainingStepState, activeUiState.isRoutineCompleted) {
        if (activeUiState.isRoutineCompleted) {
            navController.navigate(WearRoutes.sync(isPostWorkout = true)) {
                popUpTo(WearRoutes.DAY_SELECTION)
            }
        } else if (activeUiState.trainingStepState is TrainingStepState.Active) {
            navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                popUpTo(WearRoutes.ACTIVE_WORKOUT) { inclusive = true }
            }
        }
    }

    WearCooldownTransitionScreen(
        trainingStepState = activeUiState.trainingStepState,
        exerciseSessions = activeUiState.exerciseSessions,
        heartRateBpm = activeUiState.currentHeartRateBpm,
        onAddExtraTime = { activeWorkoutViewModel.addExtraCooldownTime() },
        onSkipRest = {
            activeWorkoutViewModel.skipCooldown()
            val state = activeWorkoutViewModel.uiState.value
            if (state.isRoutineCompleted) {
                navController.navigate(WearRoutes.sync(isPostWorkout = true)) {
                    popUpTo(WearRoutes.DAY_SELECTION)
                }
            } else {
                navController.navigate(WearRoutes.activeWorkout(planId, day)) {
                    popUpTo(WearRoutes.ACTIVE_WORKOUT) { inclusive = true }
                }
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
