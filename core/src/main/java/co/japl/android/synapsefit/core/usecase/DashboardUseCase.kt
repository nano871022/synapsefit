@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

class DashboardUseCase(
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) : IDashboardUseCase {
    private val validateActivePlanSessionsUseCase =
        ValidateActivePlanSessionsUseCase(workoutPlanRepositoryPort, workoutLogRepositoryPort)

    override fun getMeasurementsHistory(): Flow<List<BodyMeasurement>> = bodyMeasurementRepositoryPort.getMeasurementsHistory()

    override fun getActivePlan(): Flow<WorkoutPlan?> = workoutPlanRepositoryPort.getActivePlan()

    override fun getAllPlans(): Flow<List<WorkoutPlan>> = workoutPlanRepositoryPort.getAllPlans()

    override fun getPlanWithExercises(planId: String): Flow<Pair<WorkoutPlan, List<Exercise>>?> =
        workoutPlanRepositoryPort.getPlanWithExercises(planId)

    override fun getLatestLogsForPlan(planId: String): Flow<List<WorkoutLog>> = workoutLogRepositoryPort.getLatestLogsForPlan(planId)

    override suspend fun validateActivePlanSessions(planId: String?): PlanSessionValidationResult? =
        validateActivePlanSessionsUseCase(planId)
}
