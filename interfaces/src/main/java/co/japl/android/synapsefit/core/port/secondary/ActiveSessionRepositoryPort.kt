package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import kotlinx.coroutines.flow.Flow

interface ActiveSessionRepositoryPort {
    fun getActiveSessionState(): Flow<ActiveWorkoutSessionState>

    fun updateActiveSessionState(state: ActiveWorkoutSessionState)

    fun clearActiveSession()
}
