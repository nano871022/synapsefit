package co.japl.android.synapsefit

import android.content.Context
import androidx.room.Room
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GenerateWorkoutPlanUseCase
import co.japl.android.synapsefit.core.usecase.GetExerciseMediaUseCase
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.SaveBodyMeasurementUseCase
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.drive.GoogleDriveAppDataAdapter
import co.japl.android.synapsefit.services.llm.MultiLlmClientAdapter
import co.japl.android.synapsefit.services.repository.BodyMeasurementRepositoryAdapter
import co.japl.android.synapsefit.services.repository.LlmConfigRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter

class DependencyContainer(context: Context) {
    private val database: SynapseFitDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            SynapseFitDatabase::class.java,
            "synapsefit_database.db",
        ).fallbackToDestructiveMigration().build()
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

    val driveSyncPort: DriveSyncPort by lazy {
        GoogleDriveAppDataAdapter()
    }

    val llmClient: LlmClientPort by lazy {
        MultiLlmClientAdapter(context)
    }

    // UseCases
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
        )
    }

    val recordWorkoutSessionUseCase: RecordWorkoutSessionUseCase by lazy {
        RecordWorkoutSessionUseCase(workoutLogRepository)
    }

    val performDriveSyncUseCase: PerformDriveSyncUseCase by lazy {
        PerformDriveSyncUseCase(driveSyncPort)
    }

    val getExerciseMediaUseCase: GetExerciseMediaUseCase by lazy {
        GetExerciseMediaUseCase(workoutPlanRepository, llmConfigRepository, llmClient)
    }
}
