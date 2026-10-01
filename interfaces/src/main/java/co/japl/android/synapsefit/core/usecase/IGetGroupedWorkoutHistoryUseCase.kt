package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import kotlinx.coroutines.flow.Flow

interface IGetGroupedWorkoutHistoryUseCase {
    operator fun invoke(): Flow<List<WorkoutHistoryGroup>>
}
