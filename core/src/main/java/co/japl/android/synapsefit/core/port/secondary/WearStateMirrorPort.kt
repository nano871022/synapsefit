package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface WearStateMirrorPort {
    val liveSyncEvents: SharedFlow<LiveSyncEvent>
    val isConnected: StateFlow<Boolean>

    suspend fun sendEvent(event: LiveSyncEvent)

    fun onEventReceived(event: LiveSyncEvent)

    fun onConnectionStateChanged(isConnected: Boolean)
}
