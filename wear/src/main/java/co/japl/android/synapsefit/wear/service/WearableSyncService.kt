package co.japl.android.synapsefit.wear.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearableSyncService {

    private val _isPhoneConnected = MutableStateFlow(true)
    val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val _pendingSyncDataCount = MutableStateFlow(0)
    val pendingSyncDataCount: StateFlow<Int> = _pendingSyncDataCount.asStateFlow()

    fun onConnectionStateChanged(isConnected: Boolean) {
        _isPhoneConnected.value = isConnected
    }

    fun queueDataForDeferredSync(exerciseId: String, reps: Int, heartRateBpm: Int) {
        _pendingSyncDataCount.value += 1
    }

    fun flushSyncQueue() {
        if (_isPhoneConnected.value) {
            _pendingSyncDataCount.value = 0
        }
    }
}
