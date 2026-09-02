@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught")

package co.japl.android.synapsefit.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.util.DateTimeUtils
import co.japl.android.synapsefit.util.MathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseLogSetUiModel(
    val setIndex: Int,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int?,
    val timestamp: Long,
)

data class ExerciseSessionDetailUiModel(
    val exerciseId: String,
    val exerciseName: String,
    val sets: List<ExerciseLogSetUiModel>,
    val averageReps: Double,
    val averageWeightKg: Double,
)

/**
 * UI model representing a grouped workout session card.
 */
data class WorkoutSessionGroupUiModel(
    val sessionId: String,
    val sessionTitle: String,
    val dateFormatted: String,
    val timestamp: Long,
    val totalExercisesCount: Int,
    val totalVolumeKg: Double,
    val exercises: List<ExerciseSessionDetailUiModel>,
)

// Legacy compatibility model kept for tests or flat representations
data class SessionHistoryUiModel(
    val id: String,
    val exerciseId: String,
    val exerciseName: String = "",
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
    val sessionGroups: List<WorkoutSessionGroupUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

class WorkoutHistoryViewModel(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val logsFlow =
                workoutLogRepositoryPort?.getAllLogs() ?: flowOf(emptyList())
            val plansFlow =
                workoutPlanRepositoryPort?.getAllPlans() ?: flowOf(emptyList())

            combine(logsFlow, plansFlow) { logs, plans ->
                val exerciseNameMap = mutableMapOf<String, String>()
                val exerciseDayMap = mutableMapOf<String, Int>()
                plans.forEach { plan ->
                    val pair = workoutPlanRepositoryPort?.getPlanWithExercises(plan.id)?.firstOrNull()
                    pair?.second?.forEach { ex ->
                        exerciseNameMap[ex.id] = ex.name
                        exerciseDayMap[ex.id] = ex.day
                    }
                }
                Triple(logs, exerciseNameMap, exerciseDayMap)
            }.collect { (logs, exerciseNameMap, exerciseDayMap) ->
                // Mapped flat logs
                val mapped =
                    logs.map { log ->
                        val nameResolved = exerciseNameMap[log.exerciseId]?.takeIf { it.isNotBlank() }
                        val fallbackName = "Ejercicio (${log.exerciseId.take(8)})"
                        SessionHistoryUiModel(
                            id = log.id,
                            exerciseId = log.exerciseId,
                            exerciseName = nameResolved ?: fallbackName,
                            repsCompleted = log.repsCompleted,
                            weightLiftedKg = log.weightLiftedKg,
                            heartRateBpm = log.heartRateBpm,
                            timestamp = log.timestamp,
                            sourceDevice = log.sourceDevice.name,
                        )
                    }

                // Group logs into sessions by session key (date + estimated day or session interval)
                // We consider logs recorded within 2 hours of each other on the same day as part of the same session
                val sortedLogs = logs.sortedByDescending { it.timestamp }
                val groups = mutableListOf<WorkoutSessionGroupUiModel>()

                val sessionClusterMap = LinkedHashMap<String, MutableList<co.japl.android.synapsefit.core.domain.model.WorkoutLog>>()
                var currentClusterKey = ""
                var lastTimestamp = 0L

                for (log in sortedLogs) {
                    val dateKey = DateTimeUtils.formatEpoch(log.timestamp, "yyyy-MM-dd")
                    // Group all logs performed on the same calendar day into the same session cluster.
                    // If date is different, start a new session cluster.
                    if (currentClusterKey.isEmpty() || !currentClusterKey.startsWith(dateKey)) {
                        currentClusterKey = "${dateKey}_${log.timestamp}"
                        sessionClusterMap[currentClusterKey] = mutableListOf()
                    }
                    sessionClusterMap[currentClusterKey]?.add(log)
                    lastTimestamp = log.timestamp
                }

                sessionClusterMap.forEach { (clusterKey, sessionLogs) ->
                    val dateStr = DateTimeUtils.formatEpoch(sessionLogs.first().timestamp, "yyyy-MM-dd")

                    // Determine exercise details for this session
                    val exerciseGroupMap = LinkedHashMap<String, MutableList<co.japl.android.synapsefit.core.domain.model.WorkoutLog>>()
                    sessionLogs.forEach { log ->
                        exerciseGroupMap.getOrPut(log.exerciseId) { mutableListOf() }.add(log)
                    }

                    var sessionDayNumber = 1
                    val exerciseDetails =
                        exerciseGroupMap.map { (exId, exLogs) ->
                            val exName = exerciseNameMap[exId]?.takeIf { it.isNotBlank() } ?: "Ejercicio (${exId.take(8)})"
                            val day = exerciseDayMap[exId] ?: 1
                            if (day > 1) sessionDayNumber = day

                            val sortedExLogs = exLogs.sortedBy { it.timestamp }
                            val setModels =
                                sortedExLogs.mapIndexed { index, l ->
                                    ExerciseLogSetUiModel(
                                        setIndex = index + 1,
                                        repsCompleted = l.repsCompleted,
                                        weightLiftedKg = l.weightLiftedKg,
                                        heartRateBpm = l.heartRateBpm,
                                        timestamp = l.timestamp,
                                    )
                                }

                            val avgReps = MathUtils.roundToDecimals(setModels.map { it.repsCompleted }.average(), 1)
                            val avgWeight = MathUtils.roundToDecimals(setModels.map { it.weightLiftedKg }.average(), 1)

                            ExerciseSessionDetailUiModel(
                                exerciseId = exId,
                                exerciseName = exName,
                                sets = setModels,
                                averageReps = avgReps,
                                averageWeightKg = avgWeight,
                            )
                        }

                    val sessionVol = sessionLogs.sumOf { it.repsCompleted * it.weightLiftedKg }
                    val dayTitle = "Día $sessionDayNumber"

                    groups.add(
                        WorkoutSessionGroupUiModel(
                            sessionId = clusterKey,
                            sessionTitle = dayTitle,
                            dateFormatted = dateStr,
                            timestamp = sessionLogs.first().timestamp,
                            totalExercisesCount = exerciseDetails.size,
                            totalVolumeKg = MathUtils.roundToDecimals(sessionVol, 1),
                            exercises = exerciseDetails,
                        ),
                    )
                }

                val totalVol = logs.sumOf { it.repsCompleted * it.weightLiftedKg }

                _uiState.update {
                    it.copy(
                        recordedSessions = mapped,
                        sessionGroups = groups,
                        weeklySessionsCount = groups.size,
                        weeklyTotalVolumeKg = MathUtils.roundToDecimals(totalVol, 1),
                        isLoading = false,
                    )
                }
            }
        }
    }
}
