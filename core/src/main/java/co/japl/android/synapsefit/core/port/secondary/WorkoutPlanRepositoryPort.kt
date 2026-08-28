package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutPlanRepositoryPort {
    fun getActivePlan(): Flow<WorkoutPlan?>

    fun getPlanWithExercises(planId: String): Flow<Pair<WorkoutPlan, List<Exercise>>?>

    fun getAllPlans(): Flow<List<WorkoutPlan>>

    suspend fun savePlan(
        plan: WorkoutPlan,
        exercises: List<Exercise>,
    )

    suspend fun setActivePlan(planId: String)

    suspend fun deletePlan(planId: String)

    suspend fun updateExerciseMedia(
        exerciseId: String,
        videoUrl: String?,
        imageUrl: String?,
    )
}
