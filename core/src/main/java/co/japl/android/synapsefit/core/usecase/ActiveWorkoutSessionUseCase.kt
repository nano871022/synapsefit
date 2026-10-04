package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

class ActiveWorkoutSessionUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
) : IActiveWorkoutSessionUseCase {
    override suspend fun saveWorkoutLog(log: WorkoutLog) {
        workoutLogRepository.saveLog(log)
    }

    override fun getActivePlan(): Flow<WorkoutPlan?> = workoutPlanRepository.getActivePlan()
}
