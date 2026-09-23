@file:Suppress("MaxLineLength", "FunctionNaming", "LongMethod", "UnusedParameter", "UnusedPrivateProperty")

package co.japl.android.synapsefit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.japl.android.synapsefit.DependencyContainer
import co.japl.android.synapsefit.app.controller.auth.GoogleAuthViewModel
import co.japl.android.synapsefit.app.controller.dashboard.DashboardViewModel
import co.japl.android.synapsefit.app.controller.history.WorkoutDetailViewModel
import co.japl.android.synapsefit.app.controller.history.WorkoutHistoryViewModel
import co.japl.android.synapsefit.app.controller.measurements.BodyMeasurementsViewModel
import co.japl.android.synapsefit.app.controller.measurements.MeasurementProgressViewModel
import co.japl.android.synapsefit.app.controller.profile.UserProfileViewModel
import co.japl.android.synapsefit.app.controller.settings.BackupSyncViewModel
import co.japl.android.synapsefit.app.controller.settings.DatabaseExplorerViewModel
import co.japl.android.synapsefit.app.controller.settings.LlmSettingsViewModel
import co.japl.android.synapsefit.app.controller.splash.SplashViewModel
import co.japl.android.synapsefit.app.controller.workout.AICoachGeneratorViewModel
import co.japl.android.synapsefit.app.controller.workout.ActiveWorkoutSessionViewModel
import co.japl.android.synapsefit.app.controller.workout.WorkoutPlanDetailViewModel
import co.japl.android.synapsefit.app.controller.workout.WorkoutPlansViewModel
import co.japl.android.synapsefit.app.ui.dashboard.DashboardScreen
import co.japl.android.synapsefit.app.ui.history.WorkoutDetailScreen
import co.japl.android.synapsefit.app.ui.history.WorkoutHistoryScreen
import co.japl.android.synapsefit.app.ui.measurements.BodyMeasurementsScreen
import co.japl.android.synapsefit.app.ui.measurements.MeasurementProgressGraphScreen
import co.japl.android.synapsefit.app.ui.profile.UserProfileScreen
import co.japl.android.synapsefit.app.ui.settings.DatabaseExplorerScreen
import co.japl.android.synapsefit.app.ui.settings.GoogleAccountScreen
import co.japl.android.synapsefit.app.ui.settings.LLMSettingsScreen
import co.japl.android.synapsefit.app.ui.splash.SplashScreen
import co.japl.android.synapsefit.app.ui.workout.AICoachGeneratorScreen
import co.japl.android.synapsefit.app.ui.workout.ActiveWorkoutSessionScreen
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlanDetailScreen
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlansScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appNavigator: AppNavigator,
    dependencyContainer: DependencyContainer,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.SPLASH,
) {
    val appContext = LocalContext.current.applicationContext

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return SplashViewModel(
                                    userProfileRepositoryPort = dependencyContainer.userProfileRepository,
                                    bodyMeasurementRepositoryPort = dependencyContainer.bodyMeasurementRepository,
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    llmConfigRepositoryPort = dependencyContainer.llmConfigRepository,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            androidx.compose.runtime.LaunchedEffect(state.isReady) {
                if (state.isReady) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
            SplashScreen()
        }

        // V1: Dashboard
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DashboardViewModel(
                                    bodyMeasurementRepositoryPort = dependencyContainer.bodyMeasurementRepository,
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    workoutLogRepositoryPort = dependencyContainer.workoutLogRepository,
                                    validateActivePlanSessionsUseCase = dependencyContainer.validateActivePlanSessionsUseCase,
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
                    navController.navigate(Routes.USER_PROFILE)
                },
            )
        }

        // Perfil de Usuario
        composable(Routes.USER_PROFILE) {
            val profileViewModel: UserProfileViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return UserProfileViewModel(
                                    getUserProfileUseCase = dependencyContainer.getUserProfileUseCase,
                                    saveUserProfileUseCase = dependencyContainer.saveUserProfileUseCase,
                                    evaluateMedicalConditionsUseCase = dependencyContainer.evaluateMedicalConditionsUseCase,
                                    getMedicalRecommendationsUseCase = dependencyContainer.getMedicalRecommendationsUseCase,
                                    appNavigator = appNavigator,
                                    context = appContext,
                                ) as T
                            }
                        },
                )

            val profileState by profileViewModel.uiState.collectAsState()

            UserProfileScreen(
                state = profileState,
                onFullNameChange = profileViewModel::onFullNameChange,
                onBirthDateChange = profileViewModel::onBirthDateChange,
                onGenderChange = profileViewModel::onGenderChange,
                onHeightCmChange = profileViewModel::onHeightCmChange,
                onBloodTypeChange = profileViewModel::onBloodTypeChange,
                onMedicalConditionsChange = profileViewModel::onMedicalConditionsChange,
                onSaveClick = profileViewModel::saveProfile,
                onRecalculateMedicalEvaluation = profileViewModel::recalculateMedicalEvaluation,
                onRetryMedicalConditions = profileViewModel::retryMedicalEvaluation,
                onDismissMedicalDialog = profileViewModel::dismissMedicalDialog,
            )
        }

        // Explorador de Base de Datos
        composable(Routes.DATABASE_EXPLORER) {
            val viewModel: DatabaseExplorerViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DatabaseExplorerViewModel(
                                    getDatabaseSummaryUseCase = dependencyContainer.getDatabaseSummaryUseCase,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            DatabaseExplorerScreen(
                state = state,
                onRefreshClick = viewModel::loadDatabaseSummary,
            )
        }

        // V2: Body Measurements Entry
        composable(Routes.MEASUREMENTS_ENTRY) {
            val viewModel: BodyMeasurementsViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
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
                            @Suppress("UNCHECKED_CAST")
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
                            @Suppress("UNCHECKED_CAST")
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
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AICoachGeneratorViewModel(
                                    generateWorkoutPlanUseCase = dependencyContainer.generateWorkoutPlanUseCase,
                                    optimizeWorkoutPromptUseCase = dependencyContainer.optimizeWorkoutPromptUseCase,
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    getExerciseMediaUseCase = dependencyContainer.getExerciseMediaUseCase,
                                    appNavigator = appNavigator,
                                    context = appContext,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            AICoachGeneratorScreen(
                state = state,
                onLocationSelected = viewModel::onLocationSelected,
                onEquipmentSelected = viewModel::onEquipmentSelected,
                onGymChainQueryChange = viewModel::onGymChainQueryChange,
                onDaysPerWeekChange = viewModel::onDaysPerWeekChange,
                onPromptContextChange = viewModel::onPromptContextChange,
                onOptimizeClick = viewModel::optimizePrompt,
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
                            @Suppress("UNCHECKED_CAST")
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
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ActiveWorkoutSessionViewModel(
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    recordWorkoutSessionUseCase = dependencyContainer.recordWorkoutSessionUseCase,
                                    workoutLogRepositoryPort = dependencyContainer.workoutLogRepository,
                                    getExerciseMediaUseCase = dependencyContainer.getExerciseMediaUseCase,
                                    context = appContext,
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
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutHistoryViewModel(
                                    workoutPlanRepositoryPort = dependencyContainer.workoutPlanRepository,
                                    getWorkoutHistorySummaryUseCase = dependencyContainer.getWorkoutHistorySummaryUseCase,
                                ) as T
                            }
                        },
                )
            val state by viewModel.uiState.collectAsState()
            WorkoutHistoryScreen(
                state = state,
                onPreviousMonthClick = viewModel::selectPreviousMonth,
                onNextMonthClick = viewModel::selectNextMonth,
                onSessionClick = { date, day ->
                    navController.navigate(Routes.workoutHistoryDetail(date, day))
                },
            )
        }

        // V8.1: Workout History Detail
        composable(
            route = Routes.WORKOUT_HISTORY_DETAIL,
            arguments =
                listOf(
                    navArgument("date") { type = NavType.StringType },
                    navArgument("day") { type = NavType.IntType },
                ),
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val day = backStackEntry.arguments?.getInt("day") ?: 1
            val viewModel: WorkoutDetailViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutDetailViewModel(
                                    getWorkoutDetailUseCase = dependencyContainer.getWorkoutDetailUseCase,
                                ) as T
                            }
                        },
                )
            androidx.compose.runtime.LaunchedEffect(date, day) {
                viewModel.loadWorkoutDetail(date, day)
            }
            val state by viewModel.uiState.collectAsState()
            WorkoutDetailScreen(
                state = state,
                onBackClick = { navController.navigateUp() },
            )
        }

        // V9: Cuenta de Google & Respaldo Nube
        composable(Routes.SETTINGS_BACKUP) {
            val syncViewModel: BackupSyncViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return BackupSyncViewModel(
                                    performDriveSyncUseCase = dependencyContainer.performDriveSyncUseCase,
                                    googleAuthRepository = dependencyContainer.googleAuthRepository,
                                    uploadDatabaseBackupUseCase = dependencyContainer.uploadDatabaseBackupUseCase,
                                    downloadAndRestoreDatabaseUseCase = dependencyContainer.downloadAndRestoreDatabaseUseCase,
                                ) as T
                            }
                        },
                )
            val authViewModel: GoogleAuthViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return GoogleAuthViewModel(
                                    googleAuthRepository = dependencyContainer.googleAuthRepository,
                                ) as T
                            }
                        },
                )

            val syncState by syncViewModel.uiState.collectAsState()
            val authState by authViewModel.uiState.collectAsState()

            GoogleAccountScreen(
                googleAuthState = authState,
                syncState = syncState,
                onGoogleLoginClick = { authViewModel.onGoogleLoginClicked(appContext) },
                onSignOutClick = authViewModel::onSignOutClicked,
                onSelectAccountClick = authViewModel::onSelectAccountClicked,
                onAddAnotherAccountClick = authViewModel::onAddAnotherAccountClicked,
                onDismissAccountSelection = authViewModel::onErrorDismissed,
                onBackupNowClick = syncViewModel::triggerBackupNow,
                onRestoreNowClick = syncViewModel::triggerRestoreNow,
                onNavigateDbExplorer = { navController.navigate(Routes.DATABASE_EXPLORER) },
            )
        }

        // V10: LLM Settings
        composable(
            route = "settings/llm?openForm={openForm}",
            arguments =
                listOf(
                    navArgument("openForm") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) { backStackEntry ->
            val openForm = backStackEntry.arguments?.getBoolean("openForm") ?: false
            val viewModel: LlmSettingsViewModel =
                viewModel(
                    factory =
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
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
                initialOpenFormDialog = openForm,
                onSaveConfig = viewModel::saveConfig,
                onFetchModels = viewModel::fetchModels,
                onActivateConfig = viewModel::setActiveConfig,
                onDeactivateConfig = viewModel::deactivateConfig,
                onDuplicateConfig = viewModel::duplicateConfig,
                onDeleteConfig = viewModel::deleteConfig,
                onUpdateConfig = viewModel::updateConfig,
            )
        }

        // V11: About Developer
        composable(Routes.SETTINGS_ABOUT) {
            val appContext = LocalContext.current
            val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            co.com.japl.homeconnect.about.ui.About(
                versionDetail = packageInfo.versionName.orEmpty(),
                applicationId = appContext.packageName,
            )
        }
    }
}
