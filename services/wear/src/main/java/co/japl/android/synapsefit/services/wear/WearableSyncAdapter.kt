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

    override fun onConnectionStateChanged(isConnected: Boolean) {
        _isPhoneConnected.value = isConnected
    }

    override fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    ) {
        _pendingSyncDataCount.value += 1
    }

    override fun flushSyncQueue() {
        if (_isPhoneConnected.value) {
            _pendingSyncDataCount.value = 0
        }
    }
}
