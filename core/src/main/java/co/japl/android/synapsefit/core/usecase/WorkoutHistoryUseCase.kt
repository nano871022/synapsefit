package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val DEFAULT_TOTAL_SESSIONS = 12

class WorkoutHistoryUseCase(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
) : IWorkoutHistoryUseCase {
    override operator fun invoke(): Flow<WorkoutHistoryData> {
        val summariesFlow = workoutLogRepositoryPort.getWorkoutSummaries()
        val plansFlow = workoutPlanRepositoryPort.getAllPlans()

        return combine(summariesFlow, plansFlow) { summaries, plans ->
            val activePlan = plans.firstOrNull { it.isActive } ?: plans.firstOrNull()
            val activePlanTitle = activePlan?.title ?: ""
            val activePlanTotalSessions = activePlan?.totalSessions ?: DEFAULT_TOTAL_SESSIONS

            WorkoutHistoryData(
                summaries = summaries,
                activePlanTitle = activePlanTitle,
                activePlanTotalSessions = activePlanTotalSessions,
            )
        }
    }
}
