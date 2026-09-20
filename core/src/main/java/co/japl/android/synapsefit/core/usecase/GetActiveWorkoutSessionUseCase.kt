package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetActiveWorkoutSessionUseCase(
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort,
) {
    operator fun invoke(): Flow<ActiveWorkoutSessionState> = activeSessionRepositoryPort.getActiveSessionState()
}
