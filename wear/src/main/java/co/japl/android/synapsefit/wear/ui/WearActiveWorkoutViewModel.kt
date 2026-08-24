package co.japl.android.synapsefit.wear.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WearActiveWorkoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WearActiveWorkoutUiState())
    val uiState: StateFlow<WearActiveWorkoutUiState> = _uiState.asStateFlow()

    fun updateExerciseName(name: String) {
        _uiState.update { it.copy(exerciseName = name) }
    }

    fun updateHeartRate(bpm: Int) {
        _uiState.update { it.copy(currentHeartRateBpm = bpm) }
    }

    fun incrementReps() {
        _uiState.update { it.copy(currentReps = it.currentReps + 1) }
    }

    fun decrementReps() {
        _uiState.update { it.copy(currentReps = (it.currentReps - 1).coerceAtLeast(0)) }
    }

    fun setSyncStatus(isSynced: Boolean) {
        _uiState.update { it.copy(isSyncedWithPhone = isSynced) }
    }
}
