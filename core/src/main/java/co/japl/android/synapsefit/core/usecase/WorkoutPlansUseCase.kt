package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

class WorkoutPlansUseCase(
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
) : IWorkoutPlansUseCase {
    override fun getAllPlans(): Flow<List<WorkoutPlan>> = workoutPlanRepository.getAllPlans()
    override suspend fun setActivePlan(planId: String) {
        workoutPlanRepository.setActivePlan(planId)
    }
    override suspend fun deletePlan(planId: String) {
        workoutPlanRepository.deletePlan(planId)
    }
}
