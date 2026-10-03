package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IWorkoutPlansUseCase {
    fun getAllPlans(): Flow<List<WorkoutPlan>>
    suspend fun setActivePlan(planId: String)
    suspend fun deletePlan(planId: String)
}
