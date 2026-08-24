package co.japl.android.synapsefit.wear.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WearActiveWorkoutViewModel(
    private val sensorPort: WearSensorPort? = null,
    private val syncPort: WearSyncPort? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(WearActiveWorkoutUiState())
    val uiState: StateFlow<WearActiveWorkoutUiState> = _uiState.asStateFlow()

    fun updateExerciseName(name: String) {
        _uiState.update { it.copy(exerciseName = name) }
    }

    fun updateHeartRate(bpm: Int) {
        sensorPort?.onHeartRateSensorChanged(bpm)
        _uiState.update { it.copy(currentHeartRateBpm = bpm) }
    }

    fun incrementReps() {
        _uiState.update { it.copy(currentReps = it.currentReps + 1) }
    }

    fun decrementReps() {
        _uiState.update { it.copy(currentReps = (it.currentReps - 1).coerceAtLeast(0)) }
    }

    fun setSyncStatus(isSynced: Boolean) {
        syncPort?.onConnectionStateChanged(isSynced)
        _uiState.update { it.copy(isSyncedWithPhone = isSynced) }
    }
}
