package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import kotlinx.coroutines.flow.Flow

interface IGetWorkoutHistorySummaryUseCase {
    operator fun invoke(): Flow<List<WorkoutSummaryItem>>
}
