package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import kotlinx.coroutines.flow.Flow

interface IGetTodayRoutineUseCase {
    operator fun invoke(): Flow<List<WorkoutSessionItem>>
}
