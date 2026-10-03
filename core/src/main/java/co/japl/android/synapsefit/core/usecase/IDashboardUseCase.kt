package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IDashboardUseCase {
    fun getMeasurementsHistory(): Flow<List<BodyMeasurement>>

    fun getActivePlan(): Flow<WorkoutPlan?>

    fun getAllPlans(): Flow<List<WorkoutPlan>>

    fun getPlanWithExercises(planId: String): Flow<Pair<WorkoutPlan, List<Exercise>>?>

    fun getLatestLogsForPlan(planId: String): Flow<List<WorkoutLog>>

    suspend fun validateActivePlanSessions(planId: String?): PlanSessionValidationResult?
}
