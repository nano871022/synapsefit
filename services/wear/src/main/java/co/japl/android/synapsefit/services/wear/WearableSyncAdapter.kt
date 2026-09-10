package co.japl.android.synapsefit.services.wear

import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearableSyncAdapter : WearSyncPort {
    private val _isPhoneConnected = MutableStateFlow(true)
    override val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val _pendingSyncDataCount = MutableStateFlow(0)
    override val pendingSyncDataCount: StateFlow<Int> = _pendingSyncDataCount.asStateFlow()

    private val pendingLogsQueue = mutableListOf<Triple<String, Int, Int>>()

    override fun onConnectionStateChanged(isConnected: Boolean) {
        _isPhoneConnected.value = isConnected
    }

    override fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    ) {
        pendingLogsQueue.add(Triple(exerciseId, reps, heartRateBpm))
        _pendingSyncDataCount.value = pendingLogsQueue.size
    }

    override fun flushSyncQueue() {
        if (_isPhoneConnected.value) {
            pendingLogsQueue.clear()
            _pendingSyncDataCount.value = 0
        }
    }
}
