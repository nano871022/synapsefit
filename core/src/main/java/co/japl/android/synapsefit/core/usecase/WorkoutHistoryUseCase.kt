package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow

class WorkoutHistoryUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) : IWorkoutHistoryUseCase {
    private val getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepository)
    private val getWorkoutHistorySummaryUseCase = GetWorkoutHistorySummaryUseCase(workoutLogRepository)

    override fun getGroupedHistory(): Flow<List<WorkoutHistoryGroup>> = getGroupedWorkoutHistoryUseCase()

    override fun getHistorySummary(): Flow<List<WorkoutSummaryItem>> = getWorkoutHistorySummaryUseCase()
}
