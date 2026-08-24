package co.japl.android.synapsefit.core.port.secondary

import kotlinx.coroutines.flow.StateFlow

interface WearSensorPort {
    val heartRateBpm: StateFlow<Int>
    val isMonitoring: StateFlow<Boolean>

    fun startHeartRateMonitoring()
    fun stopHeartRateMonitoring()
    fun onHeartRateSensorChanged(bpm: Int)
}
