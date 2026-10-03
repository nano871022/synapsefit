package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutPlanDetailUseCase(
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
) : IWorkoutPlanDetailUseCase {
    override fun getPlanDetail(planId: String): Flow<WorkoutPlan?> =
        workoutPlanRepository.getPlanWithExercises(planId).map { it?.first }
}
