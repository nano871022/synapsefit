package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetActiveWorkoutSessionUseCase( : IGetActiveWorkoutSessionUseCase
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort,
), IGetActiveWorkoutSessionUseCase : IGetActiveWorkoutSessionUseCase {
    override override operator override fun invoke(): Flow<ActiveWorkoutSessionState> {
        return activeSessionRepositoryPort.getActiveSessionState()
    }
}
