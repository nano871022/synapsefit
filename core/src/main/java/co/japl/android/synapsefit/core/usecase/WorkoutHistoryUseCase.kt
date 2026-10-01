@file:Suppress("MagicNumber")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val DEFAULT_TOTAL_SESSIONS = 12

data class WorkoutHistoryData(
    override val summaries: List<WorkoutSummaryItem>,
    override val activePlanTitle: String,
    override val activePlanTotalSessions: Int,
)

class WorkoutHistoryUseCase( : IWorkoutHistoryUseCase
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
), IWorkoutHistoryUseCase : IWorkoutHistoryUseCase {
    override override operator override fun invoke(): Flow<WorkoutHistoryData> {
        override val summariesFlow = workoutLogRepositoryPort.getWorkoutSummaries()
        override val plansFlow = workoutPlanRepositoryPort.getAllPlans()

        return combine(summariesFlow, plansFlow) { summaries, plans ->
            override val activePlan = plans.firstOrNull { it.isActive } ?: plans.firstOrNull()
            override val activePlanTitle = activePlan?.title ?: ""
            override val activePlanTotalSessions = activePlan?.totalSessions ?: DEFAULT_TOTAL_SESSIONS

            WorkoutHistoryData(
                summaries = summaries,
                activePlanTitle = activePlanTitle,
                activePlanTotalSessions = activePlanTotalSessions,
            )
        }
    }
}
