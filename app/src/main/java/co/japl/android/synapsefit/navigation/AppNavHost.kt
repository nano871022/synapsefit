@file:Suppress("FunctionNaming", "LongMethod", "UnusedParameter")

package co.japl.android.synapsefit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.japl.android.synapsefit.DependencyContainer
import co.japl.android.synapsefit.app.ui.dashboard.DashboardScreen
import co.japl.android.synapsefit.app.ui.dashboard.DashboardViewModel
import co.japl.android.synapsefit.app.ui.history.WorkoutHistoryScreen
import co.japl.android.synapsefit.app.ui.history.WorkoutHistoryViewModel
import co.japl.android.synapsefit.app.ui.measurements.BodyMeasurementsScreen
import co.japl.android.synapsefit.app.ui.measurements.BodyMeasurementsViewModel
import co.japl.android.synapsefit.app.ui.measurements.MeasurementProgressGraphScreen
import co.japl.android.synapsefit.app.ui.measurements.MeasurementProgressViewModel
import co.japl.android.synapsefit.app.ui.settings.AboutDeveloperScreen
import co.japl.android.synapsefit.app.ui.settings.AboutDeveloperViewModel
import co.japl.android.synapsefit.app.ui.settings.BackupSyncScreen
import co.japl.android.synapsefit.app.ui.settings.BackupSyncViewModel
import co.japl.android.synapsefit.app.ui.settings.LLMSettingsScreen
import co.japl.android.synapsefit.app.ui.settings.LlmSettingsViewModel
import co.japl.android.synapsefit.app.ui.workout.AICoachGeneratorScreen
import co.japl.android.synapsefit.app.ui.workout.AICoachGeneratorViewModel
import co.japl.android.synapsefit.app.ui.workout.ActiveWorkoutSessionScreen
import co.japl.android.synapsefit.app.ui.workout.ActiveWorkoutSessionViewModel
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlanDetailScreen
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlanDetailViewModel
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlansScreen
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlansViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    appNavigator: AppNavigator,
    dependencyContainer: DependencyContainer,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.DASHBOARD,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // V1: Dashboard
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DashboardViewModel(
                                    bodyMeasurementRepositoryPort = dependencyContainer.bodyMeasurementRepository,
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            DashboardScreen(
                state = state,
                onStartWorkoutClick = { planId ->
                    navController.navigate(Routes.workoutActive(planId))
                },
                onLogMeasurementClick = {
                    navController.navigate(Routes.MEASUREMENTS_ENTRY)
                },
                onProfileClick = {
                    navController.navigate(Routes.SETTINGS_ABOUT)
                },
            )
        }

        // V2: Body Measurements Entry
        composable(Routes.MEASUREMENTS_ENTRY) {
            val viewModel: BodyMeasurementsViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return BodyMeasurementsViewModel(
                                    saveBodyMeasurementUseCase = dependencyContainer.saveBodyMeasurementUseCase,
                                    bodyMeasurementRepositoryPort = dependencyContainer.bodyMeasurementRepository,
                                    appNavigator = appNavigator,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            BodyMeasurementsScreen(
                state = state,
                onWeightChange = viewModel::onWeightChange,
                onChestChange = viewModel::onChestChange,
                onWaistChange = viewModel::onWaistChange,
                onHipChange = viewModel::onHipChange,
                onBicepLeftChange = viewModel::onBicepLeftChange,
                onBicepRightChange = viewModel::onBicepRightChange,
                onThighLeftChange = viewModel::onThighLeftChange,
                onThighRightChange = viewModel::onThighRightChange,
                onNotesChange = viewModel::onNotesChange,
                onSaveClick = viewModel::saveMeasurement,
                onViewGraphClick = {
                    navController.navigate(Routes.MEASUREMENTS_PROGRESS)
                },
                onOpenPopupClick = viewModel::openPopup,
                onClosePopupClick = viewModel::closePopup,
            )
        }

        // V3: Measurement Progress Graph
        composable(Routes.MEASUREMENTS_PROGRESS) {
            val viewModel: MeasurementProgressViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MeasurementProgressViewModel(
                                    bodyMeasurementRepositoryPort = dependencyContainer.bodyMeasurementRepository,
                                    appNavigator = appNavigator,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            MeasurementProgressGraphScreen(
                state = state,
                onMetricSelected = viewModel::onMetricSelected,
                onTimeRangeSelected = viewModel::onTimeRangeSelected,
            )
        }

        // V4: Workout Plans
        composable(Routes.WORKOUT_PLANS) {
            val viewModel: WorkoutPlansViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutPlansViewModel(
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    appNavigator = appNavigator,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            WorkoutPlansScreen(
                state = state,
                onPlanClick = { planId ->
                    navController.navigate(Routes.workoutDetail(planId))
                },
                onGeneratePlanClick = {
                    navController.navigate(Routes.WORKOUT_AI_GENERATOR)
                },
            )
        }

        // V5: AI Coach Generator
        composable(Routes.WORKOUT_AI_GENERATOR) {
            val viewModel: AICoachGeneratorViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AICoachGeneratorViewModel(
                                    generateWorkoutPlanUseCase = dependencyContainer.generateWorkoutPlanUseCase,
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    appNavigator = appNavigator,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            AICoachGeneratorScreen(
                state = state,
                onEnvironmentSelected = viewModel::onEnvironmentSelected,
                onGymChainQueryChange = viewModel::onGymChainQueryChange,
                onDaysPerWeekChange = viewModel::onDaysPerWeekChange,
                onPromptContextChange = viewModel::onPromptContextChange,
                onGenerateClick = viewModel::generatePlan,
                onAcceptClick = viewModel::acceptPlan,
                onDiscardClick = viewModel::discardPlan,
            )
        }

        // V6: Workout Plan Detail
        composable(
            route = Routes.WORKOUT_DETAIL,
            arguments = listOf(navArgument("planId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val viewModel: WorkoutPlanDetailViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutPlanDetailViewModel(
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                ) as T
                            }
                        },
                )
            viewModel.loadPlanDetail(planId)
            val state by viewModel.uiState.collectAsState()
            WorkoutPlanDetailScreen(
                state = state,
                onStartSessionClick = { id ->
                    navController.navigate(Routes.workoutActive(id))
                },
            )
        }

        // V7: Active Workout Session
        composable(
            route = Routes.WORKOUT_ACTIVE,
            arguments = listOf(navArgument("planId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val viewModel: ActiveWorkoutSessionViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ActiveWorkoutSessionViewModel(
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    recordWorkoutSessionUseCase = dependencyContainer.recordWorkoutSessionUseCase,
                                    llmConfigRepositoryPort = dependencyContainer.llmConfigRepository,
                                    llmClientPort = dependencyContainer.llmClient,
                                    workoutLogRepositoryPort = dependencyContainer.workoutLogRepository,
                                ) as T
                            }
                        },
                )
            androidx.compose.runtime.LaunchedEffect(planId) {
                viewModel.startSession(planId)
            }
            val state by viewModel.uiState.collectAsState()
            ActiveWorkoutSessionScreen(
                state = state,
                onSetRepsChange = viewModel::onSetRepsChange,
                onSetWeightChange = viewModel::onSetWeightChange,
                onCompleteSet = viewModel::completeSet,
                onNextSetOrExercise = viewModel::nextSetOrExercise,
                onFinishSession = {
                    viewModel.finishSession()
                    navController.navigateUp()
                },
                onOpenImagePopup = viewModel::showImagePopup,
                onCloseImagePopup = viewModel::hideImagePopup,
            )
        }

        // V8: Workout History
        composable(Routes.WORKOUT_HISTORY) {
            val viewModel: WorkoutHistoryViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutHistoryViewModel(
                                    workoutLogRepositoryPort = dependencyContainer.workoutLogRepository,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            WorkoutHistoryScreen(state = state)
        }

        // V9: Backup Sync
        composable(Routes.SETTINGS_BACKUP) {
            val viewModel: BackupSyncViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return BackupSyncViewModel(
                                    performDriveSyncUseCase = dependencyContainer.performDriveSyncUseCase,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            BackupSyncScreen(
                state = state,
                onBackupNowClick = { viewModel.triggerBackup(byteArrayOf()) },
            )
        }

        // V10: LLM Settings
        composable(Routes.SETTINGS_LLM) {
            val viewModel: LlmSettingsViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LlmSettingsViewModel(
                                    llmConfigRepositoryPort = dependencyContainer.llmConfigRepository,
                                    llmClientPort = dependencyContainer.llmClient,
                                    appNavigator = appNavigator,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            LLMSettingsScreen(
                state = state,
                onSaveConfig = viewModel::saveConfig,
                onFetchModels = viewModel::fetchModels,
                onActivateConfig = viewModel::setActiveConfig,
                onDuplicateConfig = viewModel::duplicateConfig,
                onDeleteConfig = viewModel::deleteConfig,
                onUpdateConfig = viewModel::updateConfig,
            )
        }

        // V11: About Developer
        composable(Routes.SETTINGS_ABOUT) {
            val viewModel: AboutDeveloperViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()
            AboutDeveloperScreen(state = state)
        }
    }
}
