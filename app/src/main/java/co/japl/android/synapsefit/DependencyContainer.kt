@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit

import android.content.Context
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.AICoachGeneratorUseCase
import co.japl.android.synapsefit.core.usecase.AboutDeveloperUseCase
import co.japl.android.synapsefit.core.usecase.ActiveWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.BodyMeasurementsUseCase
import co.japl.android.synapsefit.core.usecase.DashboardUseCase
import co.japl.android.synapsefit.core.usecase.DatabaseExplorerUseCase
import co.japl.android.synapsefit.core.usecase.DownloadAndRestoreDatabaseUseCase
import co.japl.android.synapsefit.core.usecase.EvaluateMedicalConditionsUseCase
import co.japl.android.synapsefit.core.usecase.GenerateWorkoutPlanUseCase
import co.japl.android.synapsefit.core.usecase.GetDatabaseSummaryUseCase
import co.japl.android.synapsefit.core.usecase.GetExerciseMediaUseCase
import co.japl.android.synapsefit.core.usecase.GetMedicalRecommendationsUseCase
import co.japl.android.synapsefit.core.usecase.GetUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.GetWorkoutDetailUseCase
import co.japl.android.synapsefit.core.usecase.GetWorkoutHistorySummaryUseCase
import co.japl.android.synapsefit.core.usecase.GoogleAccountUseCase
import co.japl.android.synapsefit.core.usecase.GoogleSignInPromptUseCase
import co.japl.android.synapsefit.core.usecase.IAICoachGeneratorUseCase
import co.japl.android.synapsefit.core.usecase.IAboutDeveloperUseCase
import co.japl.android.synapsefit.core.usecase.IActiveWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.IBodyMeasurementsUseCase
import co.japl.android.synapsefit.core.usecase.IDashboardUseCase
import co.japl.android.synapsefit.core.usecase.IDatabaseExplorerUseCase
import co.japl.android.synapsefit.core.usecase.IGoogleAccountUseCase
import co.japl.android.synapsefit.core.usecase.IGoogleSignInPromptUseCase
import co.japl.android.synapsefit.core.usecase.ILLMSettingsUseCase
import co.japl.android.synapsefit.core.usecase.IMeasurementProgressGraphUseCase
import co.japl.android.synapsefit.core.usecase.ISplashUseCase
import co.japl.android.synapsefit.core.usecase.IUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.IWorkoutDetailUseCase
import co.japl.android.synapsefit.core.usecase.IWorkoutHistoryUseCase
import co.japl.android.synapsefit.core.usecase.IWorkoutPlanDetailUseCase
import co.japl.android.synapsefit.core.usecase.IWorkoutPlansUseCase
import co.japl.android.synapsefit.core.usecase.LLMSettingsUseCase
import co.japl.android.synapsefit.core.usecase.MeasurementProgressGraphUseCase
import co.japl.android.synapsefit.core.usecase.OptimizeWorkoutPromptUseCase
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.SaveBodyMeasurementUseCase
import co.japl.android.synapsefit.core.usecase.SaveUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.SplashUseCase
import co.japl.android.synapsefit.core.usecase.UploadDatabaseBackupUseCase
import co.japl.android.synapsefit.core.usecase.UserProfileUseCase
import co.japl.android.synapsefit.core.usecase.ValidateActivePlanSessionsUseCase
import co.japl.android.synapsefit.core.usecase.WorkoutDetailUseCase
import co.japl.android.synapsefit.core.usecase.WorkoutHistoryUseCase
import co.japl.android.synapsefit.core.usecase.WorkoutPlanDetailUseCase
import co.japl.android.synapsefit.core.usecase.WorkoutPlansUseCase
import co.japl.android.synapsefit.services.drive.GoogleAuthRepositoryImpl
import co.japl.android.synapsefit.services.drive.GoogleDriveAppDataAdapter
import co.japl.android.synapsefit.services.llm.MultiLlmClientAdapter
import co.japl.android.synapsefit.services.repository.BodyMeasurementRepositoryAdapter
import co.japl.android.synapsefit.services.repository.LlmConfigRepositoryAdapter
import co.japl.android.synapsefit.services.repository.RoomDatabaseManagerAdapter
import co.japl.android.synapsefit.services.repository.UserProfileRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter

class DependencyContainer(private val context: Context) {
    val databaseManagerPort: DatabaseManagerPort by lazy {
        RoomDatabaseManagerAdapter(context)
    }

    val bodyMeasurementRepository: BodyMeasurementRepositoryPort by lazy {
        BodyMeasurementRepositoryAdapter(context)
    }

    val userProfileRepository: UserProfileRepositoryPort by lazy {
        UserProfileRepositoryAdapter(context)
    }

    val workoutPlanRepository: WorkoutPlanRepositoryPort by lazy {
        WorkoutPlanRepositoryAdapter(context)
    }

    val workoutLogRepository: WorkoutLogRepositoryPort by lazy {
        WorkoutLogRepositoryAdapter(context)
    }

    val llmConfigRepository: LlmConfigRepositoryPort by lazy {
        LlmConfigRepositoryAdapter(context)
    }

    val llmClient: LlmClientPort by lazy {
        MultiLlmClientAdapter()
    }

    val googleAuthRepository: GoogleAuthRepository by lazy {
        GoogleAuthRepositoryImpl(context)
    }

    val driveSyncPort: DriveSyncPort by lazy {
        GoogleDriveAppDataAdapter(context, googleAuthRepository)
    }

    val dashboardUseCase: IDashboardUseCase by lazy {
        DashboardUseCase(
            bodyMeasurementRepository,
            workoutPlanRepository,
            workoutLogRepository,
        )
    }

    val userProfileUseCase: IUserProfileUseCase by lazy {
        UserProfileUseCase(userProfileRepository, llmConfigRepository, llmClient)
    }

    val bodyMeasurementsUseCase: IBodyMeasurementsUseCase by lazy {
        BodyMeasurementsUseCase(bodyMeasurementRepository)
    }

    val measurementProgressGraphUseCase: IMeasurementProgressGraphUseCase by lazy {
        MeasurementProgressGraphUseCase(bodyMeasurementRepository)
    }

    val workoutPlansUseCase: IWorkoutPlansUseCase by lazy {
        WorkoutPlansUseCase(workoutPlanRepository)
    }

    val aiCoachGeneratorUseCase: IAICoachGeneratorUseCase by lazy {
        AICoachGeneratorUseCase(
            llmConfigRepository,
            llmClient,
            workoutPlanRepository,
            userProfileRepository,
        )
    }

    val workoutPlanDetailUseCase: IWorkoutPlanDetailUseCase by lazy {
        WorkoutPlanDetailUseCase(workoutPlanRepository)
    }

    val activeWorkoutSessionUseCase: IActiveWorkoutSessionUseCase by lazy {
        ActiveWorkoutSessionUseCase(workoutLogRepository, workoutPlanRepository)
    }

    val workoutHistoryUseCase: IWorkoutHistoryUseCase by lazy {
        WorkoutHistoryUseCase(workoutLogRepository)
    }

    val workoutDetailUseCase: IWorkoutDetailUseCase by lazy {
        WorkoutDetailUseCase(workoutLogRepository)
    }

    val googleAccountUseCase: IGoogleAccountUseCase by lazy {
        GoogleAccountUseCase(googleAuthRepository, driveSyncPort, databaseManagerPort)
    }

    val googleSignInPromptUseCase: IGoogleSignInPromptUseCase by lazy {
        GoogleSignInPromptUseCase(googleAuthRepository)
    }

    val databaseExplorerUseCase: IDatabaseExplorerUseCase by lazy {
        DatabaseExplorerUseCase(databaseManagerPort)
    }

    val llmSettingsUseCase: ILLMSettingsUseCase by lazy {
        LLMSettingsUseCase(llmConfigRepository)
    }

    val aboutDeveloperUseCase: IAboutDeveloperUseCase by lazy {
        AboutDeveloperUseCase()
    }

    val splashUseCase: ISplashUseCase by lazy {
        SplashUseCase(googleAuthRepository, driveSyncPort, databaseManagerPort)
    }

    // Retained for legacy components where needed
    val validateActivePlanSessionsUseCase: ValidateActivePlanSessionsUseCase by lazy {
        ValidateActivePlanSessionsUseCase(workoutPlanRepository, workoutLogRepository)
    }

    val recordWorkoutSessionUseCase: RecordWorkoutSessionUseCase by lazy {
        RecordWorkoutSessionUseCase(workoutLogRepository)
    }

    val getExerciseMediaUseCase: GetExerciseMediaUseCase by lazy {
        GetExerciseMediaUseCase(workoutPlanRepository)
    }

    val getWorkoutHistorySummaryUseCase: GetWorkoutHistorySummaryUseCase by lazy {
        GetWorkoutHistorySummaryUseCase(workoutLogRepository)
    }

    val getWorkoutDetailUseCase: GetWorkoutDetailUseCase by lazy {
        GetWorkoutDetailUseCase(workoutLogRepository)
    }

    val getUserProfileUseCase: GetUserProfileUseCase by lazy {
        GetUserProfileUseCase(userProfileRepository)
    }

    val saveUserProfileUseCase: SaveUserProfileUseCase by lazy {
        SaveUserProfileUseCase(userProfileRepository)
    }

    val evaluateMedicalConditionsUseCase: EvaluateMedicalConditionsUseCase by lazy {
        EvaluateMedicalConditionsUseCase(userProfileRepository, llmConfigRepository, llmClient)
    }

    val getMedicalRecommendationsUseCase: GetMedicalRecommendationsUseCase by lazy {
        GetMedicalRecommendationsUseCase(userProfileRepository)
    }

    val getDatabaseSummaryUseCase: GetDatabaseSummaryUseCase by lazy {
        GetDatabaseSummaryUseCase(databaseManagerPort)
    }

    val saveBodyMeasurementUseCase: SaveBodyMeasurementUseCase by lazy {
        SaveBodyMeasurementUseCase(bodyMeasurementRepository)
    }

    val generateWorkoutPlanUseCase: GenerateWorkoutPlanUseCase by lazy {
        GenerateWorkoutPlanUseCase(
            llmConfigRepository,
            llmClient,
            workoutPlanRepository,
            userProfileRepository = userProfileRepository,
        )
    }

    val optimizeWorkoutPromptUseCase: OptimizeWorkoutPromptUseCase by lazy {
        OptimizeWorkoutPromptUseCase(llmConfigRepository, llmClient)
    }

    val performDriveSyncUseCase: PerformDriveSyncUseCase by lazy {
        PerformDriveSyncUseCase(driveSyncPort)
    }

    val uploadDatabaseBackupUseCase: UploadDatabaseBackupUseCase by lazy {
        UploadDatabaseBackupUseCase(databaseManagerPort, driveSyncPort)
    }

    val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase by lazy {
        DownloadAndRestoreDatabaseUseCase(driveSyncPort, databaseManagerPort)
    }
}
