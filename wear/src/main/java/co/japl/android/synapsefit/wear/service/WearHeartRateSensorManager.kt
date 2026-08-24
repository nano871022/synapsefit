package co.japl.android.synapsefit.wear.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearHeartRateSensorManager {

    private val _heartRateBpm = MutableStateFlow(0)
    val heartRateBpm: StateFlow<Int> = _heartRateBpm.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    fun startHeartRateMonitoring() {
        _isMonitoring.value = true
    }

    fun stopHeartRateMonitoring() {
        _isMonitoring.value = false
    }

    fun onHeartRateSensorChanged(bpm: Int) {
        if (_isMonitoring.value) {
            _heartRateBpm.value = bpm
        }
    }
}
