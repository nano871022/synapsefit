package co.japl.android.synapsefit.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.util.MathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionHistoryUiModel(
    val id: String,
    val exerciseId: String,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int?,
    val timestamp: Long,
    val sourceDevice: String,
)

data class WorkoutHistoryUiState(
    val selectedYearMonth: String = "",
    val weeklySessionsCount: Int = 0,
    val weeklyTotalVolumeKg: Double = 0.0,
    val recordedSessions: List<SessionHistoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

class WorkoutHistoryViewModel(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val logs =
                workoutLogRepositoryPort?.getLogsForDateRange(0L, Long.MAX_VALUE)?.let { flow ->
                    flow.firstOrNull()
                } ?: emptyList()

            val mapped =
                logs.map { log ->
                    SessionHistoryUiModel(
                        id = log.id,
                        exerciseId = log.exerciseId,
                        repsCompleted = log.repsCompleted,
                        weightLiftedKg = log.weightLiftedKg,
                        heartRateBpm = log.heartRateBpm,
                        timestamp = log.timestamp,
                        sourceDevice = log.sourceDevice.name,
                    )
                }

            val totalVol = logs.sumOf { it.repsCompleted * it.weightLiftedKg }

            _uiState.update {
                it.copy(
                    recordedSessions = mapped,
                    weeklySessionsCount = mapped.size,
                    weeklyTotalVolumeKg = MathUtils.roundToDecimals(totalVol, 1),
                    isLoading = false,
                )
            }
        }
    }
}
