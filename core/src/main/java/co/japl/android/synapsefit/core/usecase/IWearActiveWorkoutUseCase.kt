package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

interface IWearActiveWorkoutUseCase {
    fun getActivePlan(): Flow<WorkoutPlan?>

    suspend fun saveWorkoutLog(log: WorkoutLog)
}

class WearActiveWorkoutUseCase(
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) : IWearActiveWorkoutUseCase {
    override fun getActivePlan(): Flow<WorkoutPlan?> = workoutPlanRepository.getActivePlan()

    override suspend fun saveWorkoutLog(log: WorkoutLog) {
        workoutLogRepository.saveLog(log)
    }
}
