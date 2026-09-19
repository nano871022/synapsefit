package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryActiveSessionAdapter : ActiveSessionRepositoryPort {
    private val sessionState = MutableStateFlow(ActiveWorkoutSessionState())

    override fun getActiveSessionState(): Flow<ActiveWorkoutSessionState> = sessionState.asStateFlow()

    override fun updateActiveSessionState(state: ActiveWorkoutSessionState) {
        sessionState.value = state
    }

    override fun clearActiveSession() {
        sessionState.value = ActiveWorkoutSessionState()
    }
}
