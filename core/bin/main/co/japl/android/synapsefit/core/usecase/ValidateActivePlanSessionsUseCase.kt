package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class PlanSessionValidationResult(
    val plan: WorkoutPlan,
    val completedSessionsCount: Int,
    val totalSessions: Int,
    val isLimitReached: Boolean,
)

class ValidateActivePlanSessionsUseCase(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

    suspend operator fun invoke(planId: String? = null): PlanSessionValidationResult? {
        val activePlan =
            if (!planId.isNullOrBlank()) {
                workoutPlanRepositoryPort.getPlanWithExercises(planId).firstOrNull()?.first
            } else {
                workoutPlanRepositoryPort.getActivePlan().firstOrNull()
                    ?: workoutPlanRepositoryPort.getAllPlans().firstOrNull()?.maxByOrNull { it.updatedAt }
            } ?: return null

        val latestLogs = workoutLogRepositoryPort.getLatestLogsForPlan(activePlan.id).firstOrNull() ?: emptyList()
        val completedSessionsCount =
            latestLogs
                .map { log -> dateFormatter.format(Instant.ofEpochMilli(log.timestamp)) }
                .distinct()
                .size

        val isLimitReached = activePlan.totalSessions > 0 && completedSessionsCount >= activePlan.totalSessions

        return PlanSessionValidationResult(
            plan = activePlan,
            completedSessionsCount = completedSessionsCount,
            totalSessions = activePlan.totalSessions,
            isLimitReached = isLimitReached,
        )
    }
}
