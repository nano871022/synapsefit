package co.japl.android.synapsefit

import android.content.Context
import androidx.room.Room
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.CheckAndRestoreBackupUseCase
import co.japl.android.synapsefit.core.usecase.DownloadAndRestoreDatabaseUseCase
import co.japl.android.synapsefit.core.usecase.EvaluateMedicalConditionsUseCase
import co.japl.android.synapsefit.core.usecase.GenerateWorkoutPlanUseCase
import co.japl.android.synapsefit.core.usecase.GetDatabaseSummaryUseCase
import co.japl.android.synapsefit.core.usecase.GetExerciseMediaUseCase
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import co.japl.android.synapsefit.core.usecase.GetMedicalRecommendationsUseCase
import co.japl.android.synapsefit.core.usecase.GetUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.GetWorkoutDetailUseCase
import co.japl.android.synapsefit.core.usecase.GetWorkoutHistorySummaryUseCase
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.SaveBodyMeasurementUseCase
import co.japl.android.synapsefit.core.usecase.SaveUserProfileUseCase
import co.japl.android.synapsefit.core.usecase.UploadDatabaseBackupUseCase
import co.japl.android.synapsefit.core.usecase.ValidateActivePlanSessionsUseCase
import co.japl.android.synapsefit.services.database.RoomDatabaseManagerAdapter
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.drive.GoogleAuthRepositoryImpl
import co.japl.android.synapsefit.services.drive.GoogleDriveAppDataAdapter
import co.japl.android.synapsefit.services.llm.MultiLlmClientAdapter
import co.japl.android.synapsefit.services.repository.BodyMeasurementRepositoryAdapter
import co.japl.android.synapsefit.services.repository.LlmConfigRepositoryAdapter
import co.japl.android.synapsefit.services.repository.UserProfileRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter
import co.japl.android.synapsefit.services.wear.WearableStateMirrorAdapter

class DependencyContainer(context: Context) {
    val wearStateMirrorPort: WearStateMirrorPort by lazy {
        WearableStateMirrorAdapter(context.applicationContext)
    }

    private val database: SynapseFitDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            SynapseFitDatabase::class.java,
            "synapsefit_database.db",
        ).addMigrations(SynapseFitDatabase.MIGRATION_6_7, SynapseFitDatabase.MIGRATION_7_8)
            .build()
    }

    val userProfileRepository: UserProfileRepositoryPort by lazy {
        UserProfileRepositoryAdapter(database.userProfileDao())
    }

    val bodyMeasurementRepository: BodyMeasurementRepositoryPort by lazy {
        BodyMeasurementRepositoryAdapter(database.bodyMeasurementDao())
    }

    val llmConfigRepository: LlmConfigRepositoryPort by lazy {
        LlmConfigRepositoryAdapter(database.llmConfigDao())
    }

    val workoutPlanRepository: WorkoutPlanRepositoryPort by lazy {
        WorkoutPlanRepositoryAdapter(database.workoutPlanDao())
    }

    val workoutLogRepository: WorkoutLogRepositoryPort by lazy {
        WorkoutLogRepositoryAdapter(database.workoutLogDao())
    }

    val googleAuthRepository: GoogleAuthRepository by lazy {
        GoogleAuthRepositoryImpl(context.applicationContext)
    }

    val driveSyncPort: DriveSyncPort by lazy {
        GoogleDriveAppDataAdapter()
    }

    val databaseManagerPort: DatabaseManagerPort by lazy {
        RoomDatabaseManagerAdapter(context, database)
    }

    val llmClient: LlmClientPort by lazy {
        MultiLlmClientAdapter(context)
    }

    // UseCases
    val getUserProfileUseCase: GetUserProfileUseCase by lazy {
        GetUserProfileUseCase(userProfileRepository)
    }

    val saveUserProfileUseCase: SaveUserProfileUseCase by lazy {
        SaveUserProfileUseCase(userProfileRepository)
    }

    val saveBodyMeasurementUseCase: SaveBodyMeasurementUseCase by lazy {
        SaveBodyMeasurementUseCase(bodyMeasurementRepository)
    }

    val generateWorkoutPlanUseCase: GenerateWorkoutPlanUseCase by lazy {
        GenerateWorkoutPlanUseCase(
            llmConfigRepository,
            llmClient,
            workoutPlanRepository,
            bodyMeasurementRepository,
            workoutLogRepository,
            userProfileRepository,
        )
    }

    val recordWorkoutSessionUseCase: RecordWorkoutSessionUseCase by lazy {
        RecordWorkoutSessionUseCase(workoutLogRepository)
    }

    val performDriveSyncUseCase: PerformDriveSyncUseCase by lazy {
        PerformDriveSyncUseCase(driveSyncPort)
    }

    val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase by lazy {
        DownloadAndRestoreDatabaseUseCase(driveSyncPort, databaseManagerPort)
    }

    val uploadDatabaseBackupUseCase: UploadDatabaseBackupUseCase by lazy {
        UploadDatabaseBackupUseCase(databaseManagerPort, driveSyncPort)
    }

    val checkAndRestoreBackupUseCase: CheckAndRestoreBackupUseCase by lazy {
        CheckAndRestoreBackupUseCase(
            googleAuthRepository,
            driveSyncPort,
            downloadAndRestoreDatabaseUseCase,
            databaseManagerPort,
        )
    }

    val getExerciseMediaUseCase: GetExerciseMediaUseCase by lazy {
        GetExerciseMediaUseCase(workoutPlanRepository, llmConfigRepository, llmClient)
    }

    val validateActivePlanSessionsUseCase: ValidateActivePlanSessionsUseCase by lazy {
        ValidateActivePlanSessionsUseCase(workoutPlanRepository, workoutLogRepository)
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

    val getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase by lazy {
        GetGroupedWorkoutHistoryUseCase(workoutLogRepository)
    }

    val getWorkoutHistorySummaryUseCase: GetWorkoutHistorySummaryUseCase by lazy {
        GetWorkoutHistorySummaryUseCase(workoutLogRepository)
    }

    val getWorkoutDetailUseCase: GetWorkoutDetailUseCase by lazy {
        GetWorkoutDetailUseCase(workoutLogRepository)
    }

    val optimizeWorkoutPromptUseCase: co.japl.android.synapsefit.core.usecase.OptimizeWorkoutPromptUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.OptimizeWorkoutPromptUseCase(llmConfigRepository, llmClient)
    }

    val dashboardUseCase: co.japl.android.synapsefit.core.usecase.IDashboardUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.DashboardUseCase(bodyMeasurementRepository, workoutPlanRepository, workoutLogRepository)
    }

    val userProfileUseCase: co.japl.android.synapsefit.core.usecase.IUserProfileUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.UserProfileUseCase(userProfileRepository, llmConfigRepository, llmClient)
    }

    val bodyMeasurementsUseCase: co.japl.android.synapsefit.core.usecase.IBodyMeasurementsUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.BodyMeasurementsUseCase(bodyMeasurementRepository)
    }

    val measurementProgressGraphUseCase: co.japl.android.synapsefit.core.usecase.IMeasurementProgressGraphUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.MeasurementProgressGraphUseCase(bodyMeasurementRepository)
    }

    val workoutPlansUseCase: co.japl.android.synapsefit.core.usecase.IWorkoutPlansUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.WorkoutPlansUseCase(workoutPlanRepository)
    }

    val aiCoachGeneratorUseCase: co.japl.android.synapsefit.core.usecase.IAICoachGeneratorUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.AICoachGeneratorUseCase(llmConfigRepository, llmClient, workoutPlanRepository, userProfileRepository)
    }

    val workoutPlanDetailUseCase: co.japl.android.synapsefit.core.usecase.IWorkoutPlanDetailUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.WorkoutPlanDetailUseCase(workoutPlanRepository)
    }

    val activeWorkoutSessionUseCase: co.japl.android.synapsefit.core.usecase.IActiveWorkoutSessionUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.ActiveWorkoutSessionUseCase(workoutLogRepository, workoutPlanRepository)
    }

    val workoutHistoryUseCase: co.japl.android.synapsefit.core.usecase.IWorkoutHistoryUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.WorkoutHistoryUseCase(workoutLogRepository)
    }

    val workoutDetailUseCase: co.japl.android.synapsefit.core.usecase.IWorkoutDetailUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.WorkoutDetailUseCase(workoutLogRepository)
    }

    val googleAccountUseCase: co.japl.android.synapsefit.core.usecase.IGoogleAccountUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.GoogleAccountUseCase(googleAuthRepository, driveSyncPort, databaseManagerPort)
    }

    val googleSignInPromptUseCase: co.japl.android.synapsefit.core.usecase.IGoogleSignInPromptUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.GoogleSignInPromptUseCase(googleAuthRepository)
    }

    val databaseExplorerUseCase: co.japl.android.synapsefit.core.usecase.IDatabaseExplorerUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.DatabaseExplorerUseCase(databaseManagerPort)
    }

    val llmSettingsUseCase: co.japl.android.synapsefit.core.usecase.ILLMSettingsUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.LLMSettingsUseCase(llmConfigRepository)
    }

    val aboutDeveloperUseCase: co.japl.android.synapsefit.core.usecase.IAboutDeveloperUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.AboutDeveloperUseCase()
    }

    val splashUseCase: co.japl.android.synapsefit.core.usecase.ISplashUseCase by lazy {
        co.japl.android.synapsefit.core.usecase.SplashUseCase(googleAuthRepository, driveSyncPort, databaseManagerPort)
    }
}