package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlanDetailUseCase {
    fun getPlanDetail(planId: String): Flow<WorkoutPlan?>
}
