package co.japl.android.synapsefit.core.port.secondary

import kotlinx.coroutines.flow.StateFlow

interface WearSyncPort {
    val isPhoneConnected: StateFlow<Boolean>
    val pendingSyncDataCount: StateFlow<Int>

    fun onConnectionStateChanged(isConnected: Boolean)

    fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    )

    fun flushSyncQueue()
}
