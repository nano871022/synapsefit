package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import kotlinx.coroutines.flow.Flow

interface IGetActiveWorkoutSessionUseCase {
    operator fun invoke(): Flow<ActiveWorkoutSessionState>
}
