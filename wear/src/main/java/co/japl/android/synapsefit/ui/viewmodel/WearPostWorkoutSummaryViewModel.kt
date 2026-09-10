package co.japl.android.synapsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class WearPostWorkoutSummaryViewModel(
    private val getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase? = null,
    private val syncPort: WearSyncPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WearPostWorkoutSummaryUiState())
    val uiState: StateFlow<WearPostWorkoutSummaryUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        observeSyncState()
        loadSummary()
    }

    private fun observeSyncState() {
        val syncPort = syncPort ?: return
        viewModelScope.launch {
            syncPort.isPhoneConnected.collect { isConnected ->
                _uiState.update { it.copy(isSyncedWithPhone = isConnected) }
                if (isConnected) {
                    syncPort.flushSyncQueue()
                }
            }
        }
        viewModelScope.launch {
            syncPort.pendingSyncDataCount.collect { pendingCount ->
                _uiState.update { it.copy(pendingSyncDataCount = pendingCount) }
            }
        }
    }

    fun loadSummaryForPlanAndDay(
        planId: String,
        day: Int,
    ) {
        val useCase = getGroupedWorkoutHistoryUseCase ?: return
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                useCase.invoke().collect { groups ->
                    val targetGroup =
                        if (planId.isNotBlank() && planId != "default") {
                            groups.firstOrNull { it.planId == planId && it.day == day }
                                ?: groups.firstOrNull { it.planId == planId }
                                ?: groups.firstOrNull()
                        } else {
                            groups.firstOrNull()
                        }

                    if (targetGroup != null) {
                        populateStateFromGroup(targetGroup)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
    }

    fun loadSummary(sessionId: String? = null) {
        val useCase = getGroupedWorkoutHistoryUseCase ?: return
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                useCase.invoke().collect { groups ->
                    val targetGroup =
                        if (sessionId != null) {
                            groups.firstOrNull { it.sessionId == sessionId } ?: groups.firstOrNull()
                        } else {
                            groups.firstOrNull()
                        }

                    if (targetGroup != null) {
                        populateStateFromGroup(targetGroup)
                        transmitWorkoutLogsToMobile(targetGroup)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
    }

    fun populateStateFromGroup(group: WorkoutHistoryGroup) {
        val allSets = group.exercises.flatMap { it.sets }
        val heartRates = allSets.mapNotNull { it.heartRateBpm }.filter { it > 0 }
        val avgHr = if (heartRates.isNotEmpty()) heartRates.average().toInt() else null
        val maxHr = if (heartRates.isNotEmpty()) heartRates.maxOrNull() else null

        val formattedDuration = formatDuration(group.totalDurationSeconds)

        _uiState.update {
            it.copy(
                planTitle = group.planTitle,
                day = group.day,
                formattedDuration = formattedDuration,
                totalVolumeKg = group.totalVolumeKg,
                averageHeartRateBpm = avgHr,
                maxHeartRateBpm = maxHr,
                exercises = group.exercises,
                isLoading = false,
            )
        }
    }

    private fun transmitWorkoutLogsToMobile(group: WorkoutHistoryGroup) {
        val syncPort = syncPort ?: return
        for (exercise in group.exercises) {
            for (set in exercise.sets) {
                syncPort.queueDataForDeferredSync(
                    exerciseId = exercise.exerciseId,
                    reps = set.repsCompleted,
                    heartRateBpm = set.heartRateBpm ?: 0,
                )
            }
        }
    }

    private fun formatDuration(seconds: Long): String {
        val mins = seconds / SECONDS_PER_MINUTE
        val secs = seconds % SECONDS_PER_MINUTE
        val hours = mins / MINUTES_PER_HOUR
        return if (hours > 0) {
            val remMins = mins % MINUTES_PER_HOUR
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, remMins, secs)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
        }
    }

    companion object {
        private const val SECONDS_PER_MINUTE = 60
        private const val MINUTES_PER_HOUR = 60
    }
}
