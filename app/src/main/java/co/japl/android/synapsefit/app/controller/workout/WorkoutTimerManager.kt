package co.japl.android.synapsefit.app.controller.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object WorkoutTimerManager {
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _restTime = MutableStateFlow<Int?>(null)
    val restTime = _restTime.asStateFlow()

    fun updateElapsedTime(seconds: Long) {
        _elapsedTime.value = seconds
    }

    fun updateRestTime(seconds: Int?) {
        _restTime.value = seconds
    }

    fun reset() {
        _elapsedTime.value = 0L
        _restTime.value = null
    }
}
