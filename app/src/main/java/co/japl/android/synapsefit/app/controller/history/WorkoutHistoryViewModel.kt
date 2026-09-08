@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught")

package co.japl.android.synapsefit.app.controller.history

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
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
import kotlin.collections.set
import kotlin.text.isNotBlank

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
    private var _data : Pair<MutableMap<String, String>, MutableMap<String, Int>> = Pair(mutableMapOf<String, String>(), mutableMapOf<String, Int>())
    val exerciseNameMap = mutableMapOf<String, String>()
    val exerciseDayMap = mutableMapOf<String, Int>()
    var listLogs = mutableListOf<WorkoutLog>()

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
                Pair(logs, plans)
            }.collect { (logs, plans) ->
                getExercises(plans)
                listLogs.addAll(logs)
            }
        }
    }
    private fun load(){
        val mapped =
            listLogs.map { log ->
                sessionHistoryMap(log,exerciseNameMap)
            }

        val sessionClusterMap = sortLogs(listLogs)

        val groups = sessionClusterMap.map { (clusterKey, sessionLogs) ->
            sessionGroup(clusterKey,sessionLogs,exerciseNameMap,exerciseDayMap)
        }

        val totalVol = listLogs.sumOf { it.repsCompleted * it.weightLiftedKg }

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

    private suspend fun getExercises(plans: List<WorkoutPlan>){

        workoutPlanRepositoryPort?.let {
            plans.forEach { plan ->

                workoutPlanRepositoryPort?.getPlanWithExercises(plan.id)?.collect { value ->
                    value?.second?.forEach { ex ->
                        exerciseNameMap[ex.id] = ex.name
                        exerciseDayMap[ex.id] = ex.day
                        Log.i(this.javaClass.name,"Exercise load ${ex.id} ${ex.name} ${ex.day}")
                    }

                }
            }
            load()
        }
    }

    private fun sessionHistoryMap(log:WorkoutLog,
                                  exerciseNameMap:MutableMap<String, String>):SessionHistoryUiModel{
        val nameResolved = exerciseNameMap[log.exerciseId]?.takeIf { it.isNotBlank() }
        val fallbackName = "Ejercicio (${log.exerciseId.take(8)})"
        return SessionHistoryUiModel(
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

    private fun sortLogs(logs: List<WorkoutLog>):LinkedHashMap<String, MutableList<co.japl.android.synapsefit.core.domain.model.WorkoutLog>>{
        val sessionClusterMap = LinkedHashMap<String, MutableList<co.japl.android.synapsefit.core.domain.model.WorkoutLog>>()
        val sortedLogs = logs.sortedByDescending { it.timestamp }
        var currentClusterKey = ""
        for (log in sortedLogs) {
            val dateKey = DateTimeUtils.formatEpoch(log.timestamp, "yyyy-MM-dd")
            if (currentClusterKey.isEmpty() || !currentClusterKey.startsWith(dateKey)) {
                currentClusterKey = "${dateKey}_${log.timestamp}"
                sessionClusterMap[currentClusterKey] = mutableListOf()
            }
            sessionClusterMap[currentClusterKey]?.add(log)
        }
        return sessionClusterMap
    }

    private fun sessionGroup(clusterKey:String,
                             sessionLogs: MutableList<WorkoutLog>,
                             exerciseNameMap:MutableMap<String, String>,
                             exerciseDayMap:MutableMap<String, Int>):WorkoutSessionGroupUiModel{
        val dateStr = DateTimeUtils.formatEpoch(sessionLogs.first().timestamp, "yyyy-MM-dd")

        val exerciseGroupMap = LinkedHashMap<String, MutableList<co.japl.android.synapsefit.core.domain.model.WorkoutLog>>()
        sessionLogs.forEach { log ->
            exerciseGroupMap.getOrPut(log.exerciseId) { mutableListOf() }.add(log)
        }

        val sessionDayNumber = mutableIntStateOf(1)
        val exerciseDetails =
            exerciseGroupMap.map { (exId, exLogs) ->
                exerciseMap(exId,exLogs,sessionDayNumber,exerciseNameMap,exerciseDayMap)
            }

        val sessionVol = sessionLogs.sumOf { it.repsCompleted * it.weightLiftedKg }
        val dayTitle = "Día $sessionDayNumber"

        return WorkoutSessionGroupUiModel(
                sessionId = clusterKey,
                sessionTitle = dayTitle,
                dateFormatted = dateStr,
                timestamp = sessionLogs.first().timestamp,
                totalExercisesCount = exerciseDetails.size,
                totalVolumeKg = MathUtils.roundToDecimals(sessionVol, 1),
                exercises = exerciseDetails,
            )
    }

    private fun exerciseMap(exId:String,
                            exLogs: MutableList<WorkoutLog>,
                            sessionDayNumber: MutableState<Int>,
                            exerciseNameMap: MutableMap<String, String>,
                            exerciseDayMap:MutableMap<String, Int>):ExerciseSessionDetailUiModel{
        val exName = exerciseNameMap[exId]?.takeIf { it.isNotBlank() } ?: "Ejercicio (${exId.take(8)})"
        val day = exerciseDayMap[exId] ?: 1
        if (day > 1) sessionDayNumber.value = day

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

        return ExerciseSessionDetailUiModel(
            exerciseId = exId,
            exerciseName = exName,
            sets = setModels,
            averageReps = avgReps,
            averageWeightKg = avgWeight,
        )
    }


}
