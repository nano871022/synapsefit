package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import kotlinx.coroutines.flow.Flow

data class WorkoutHistoryData(
    val summaries: List<WorkoutSummaryItem>,
    val activePlanTitle: String,
    val activePlanTotalSessions: Int,
)

interface IWorkoutHistoryUseCase {
    operator fun invoke(): Flow<WorkoutHistoryData>
}
