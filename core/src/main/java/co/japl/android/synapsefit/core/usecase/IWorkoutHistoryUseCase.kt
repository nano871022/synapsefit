package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import kotlinx.coroutines.flow.Flow

interface IWorkoutHistoryUseCase {
    fun getGroupedHistory(): Flow<List<WorkoutHistoryGroup>>
    fun getHistorySummary(): Flow<List<WorkoutSummaryItem>>
}
