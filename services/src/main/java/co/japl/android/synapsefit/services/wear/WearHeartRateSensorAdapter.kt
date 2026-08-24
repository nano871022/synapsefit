package co.japl.android.synapsefit.services.wear

import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearHeartRateSensorAdapter : WearSensorPort {

    private val _heartRateBpm = MutableStateFlow(0)
    override val heartRateBpm: StateFlow<Int> = _heartRateBpm.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    override val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    override fun startHeartRateMonitoring() {
        _isMonitoring.value = true
    }

    override fun stopHeartRateMonitoring() {
        _isMonitoring.value = false
    }

    override fun onHeartRateSensorChanged(bpm: Int) {
        if (_isMonitoring.value) {
            _heartRateBpm.value = bpm
        }
    }
}
