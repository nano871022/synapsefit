package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import kotlinx.coroutines.flow.Flow

interface IWorkoutDetailUseCase {
    operator fun invoke(
        date: String,
        day: Int,
    ): Flow<WorkoutDetailGroup?>
}
