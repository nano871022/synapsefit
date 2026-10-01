package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan

data class PlanSessionValidationResult(
    val plan: WorkoutPlan,
    val completedSessionsCount: Int,
    val totalSessions: Int,
    val isLimitReached: Boolean,
)

interface IValidateActivePlanSessionsUseCase {
    suspend operator fun invoke(planId: String?): PlanSessionValidationResult?
}
