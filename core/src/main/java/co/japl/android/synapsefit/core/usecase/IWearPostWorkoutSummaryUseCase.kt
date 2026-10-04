package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort

interface IWearPostWorkoutSummaryUseCase {
    suspend fun saveWorkoutLog(log: WorkoutLog)
}

class WearPostWorkoutSummaryUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) : IWearPostWorkoutSummaryUseCase {
    override suspend fun saveWorkoutLog(log: WorkoutLog) {
        workoutLogRepository.saveLog(log)
    }
}
