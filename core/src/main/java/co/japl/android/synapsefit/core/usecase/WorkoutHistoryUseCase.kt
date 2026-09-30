package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class WorkoutHistoryData(
    val summaries: List<WorkoutSummaryItem>,
    val activePlanTitle: String,
    val activePlanTotalSessions: Int,
)

class WorkoutHistoryUseCase(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
) {
    operator fun invoke(): Flow<WorkoutHistoryData> {
        val summariesFlow = workoutLogRepositoryPort.getWorkoutSummaries()
        val plansFlow = workoutPlanRepositoryPort.getAllPlans()

        return combine(summariesFlow, plansFlow) { summaries, plans ->
            val activePlan = plans.firstOrNull { it.isActive } ?: plans.firstOrNull()
            val activePlanTitle = activePlan?.title ?: ""
            val activePlanTotalSessions = activePlan?.totalSessions ?: 12

            WorkoutHistoryData(
                summaries = summaries,
                activePlanTitle = activePlanTitle,
                activePlanTotalSessions = activePlanTotalSessions,
            )
        }
    }
}
