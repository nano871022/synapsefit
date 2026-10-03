package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IWorkoutPlanDetailUseCase {
    fun getPlanDetail(planId: String): Flow<WorkoutPlan?>
}
