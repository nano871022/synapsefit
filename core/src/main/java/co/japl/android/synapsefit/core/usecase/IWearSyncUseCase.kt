package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort

interface IWearSyncUseCase {
    suspend fun syncWorkoutLog(log: WorkoutLog)
}

class WearSyncUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) : IWearSyncUseCase {
    override suspend fun syncWorkoutLog(log: WorkoutLog) {
        workoutLogRepository.saveLog(log)
    }
}
