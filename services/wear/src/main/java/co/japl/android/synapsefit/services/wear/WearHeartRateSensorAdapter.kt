package co.japl.android.synapsefit.services.wear

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearHeartRateSensorAdapter(
    private val context: Context? = null,
) : WearSensorPort, SensorEventListener {
    private val _heartRateBpm = MutableStateFlow(0)
    override val heartRateBpm: StateFlow<Int> = _heartRateBpm.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    override val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private var sensorManager: SensorManager? = null
    private var heartRateSensor: Sensor? = null

    init {
        context?.let { ctx ->
            sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            heartRateSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        }
    }

    override fun startHeartRateMonitoring() {
        _isMonitoring.value = true
        heartRateSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun stopHeartRateMonitoring() {
        _isMonitoring.value = false
        sensorManager?.unregisterListener(this)
    }

    override fun onHeartRateSensorChanged(bpm: Int) {
        if (_isMonitoring.value) {
            _heartRateBpm.value = bpm
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!_isMonitoring.value || event == null) return
        if (event.sensor.type == Sensor.TYPE_HEART_RATE && event.values.isNotEmpty()) {
            val bpm = event.values[0].toInt()
            if (bpm > 0) {
                _heartRateBpm.value = bpm
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
        // No-op for accuracy updates
    }
}
