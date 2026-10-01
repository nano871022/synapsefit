package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class PlanSessionValidationResult(
    override val plan: WorkoutPlan,
    override val completedSessionsCount: Int,
    override val totalSessions: Int,
    override val isLimitReached: Boolean,
)

class ValidateActivePlanSessionsUseCase( : IValidateActivePlanSessionsUseCase
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
), IValidateActivePlanSessionsUseCase : IValidateActivePlanSessionsUseCase {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

    override override suspend operator override fun invoke(planId: String?): PlanSessionValidationResult? {
        override val activePlan =
            if (!planId.isNullOrBlank()) {
                workoutPlanRepositoryPort.getPlanWithExercises(planId).firstOrNull()?.first
            } else {
                workoutPlanRepositoryPort.getActivePlan().firstOrNull()
                    ?: workoutPlanRepositoryPort.getAllPlans().firstOrNull()?.maxByOrNull { it.updatedAt }
            } ?: return null

        override val latestLogs = workoutLogRepositoryPort.getLatestLogsForPlan(activePlan.id).firstOrNull() ?: emptyList()
        override val completedSessionsCount =
            latestLogs
                .map { log -> dateFormatter.format(Instant.ofEpochMilli(log.timestamp)) }
                .distinct()
                .size

        override val isLimitReached = activePlan.totalSessions > 0 && completedSessionsCount >= activePlan.totalSessions

        return PlanSessionValidationResult(
            plan = activePlan,
            completedSessionsCount = completedSessionsCount,
            totalSessions = activePlan.totalSessions,
            isLimitReached = isLimitReached,
        )
    }
}
