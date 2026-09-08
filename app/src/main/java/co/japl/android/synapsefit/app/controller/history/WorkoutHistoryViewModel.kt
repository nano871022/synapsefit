@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught")

package co.japl.android.synapsefit.app.controller.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
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
import java.util.Calendar
import java.util.Locale
import kotlin.collections.set
import kotlin.text.isNotBlank

data class CalendarDayUiModel(
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean,
    val hasWorkout: Boolean,
    val dateIso: String,
)

data class ActivePlanStatsUiModel(
    val planTitle: String = "",
    val currentWeek: Int = 1,
    val totalWeeks: Int = 8,
    val completedSessionsCount: Int = 0,
    val totalSessionsGoal: Int = 12,
    val totalVolumeTons: Double = 0.0,
    val totalHours: Double = 0.0,
)

data class GlobalHistoryStatsUiModel(
    val totalWorkoutsCount: Int = 0,
    val totalVolumeFormatted: String = "0 kg",
    val totalHours: Double = 0.0,
)

data class ExerciseLogSetUiModel(
    val setIndex: Int,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int?,
    val durationSeconds: Long = 0L,
    val timestamp: Long,
)

data class ExerciseSessionDetailUiModel(
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String = "",
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
    val durationMinutes: Int,
    val timestamp: Long,
    val muscleGroups: List<String>,
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
    val sourceDevice: String,
    val timestamp: Long,
)

data class WorkoutHistoryUiState(
    val selectedYearMonth: String = "",
    val selectedYearMonthDisplay: String = "",
    val calendarGrid: List<CalendarDayUiModel> = emptyList(),
    val weeklySessionsCount: Int = 0,
    val weeklyTotalHours: Double = 0.0,
    val weeklyTotalVolumeKg: Double = 0.0,
    val activePlanStats: ActivePlanStatsUiModel = ActivePlanStatsUiModel(),
    val globalHistoryStats: GlobalHistoryStatsUiModel = GlobalHistoryStatsUiModel(),
    val recordedSessions: List<SessionHistoryUiModel> = emptyList(),
    val sessionGroups: List<WorkoutSessionGroupUiModel> = emptyList(),
    val filteredSessionGroups: List<WorkoutSessionGroupUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

class WorkoutHistoryViewModel(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    val exerciseNameMap = mutableMapOf<String, String>()
    val exerciseDayMap = mutableMapOf<String, Int>()
    val exerciseMuscleMap = mutableMapOf<String, String>()
    var listLogs = mutableListOf<WorkoutLog>()

    private var activePlanTitle: String = ""
    private var activePlanTotalSessions: Int = 12

    init {
        val currentCal = Calendar.getInstance()
        val yearMonth = String.format(Locale.ROOT, "%04d-%02d", currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH) + 1)
        _uiState.update {
            it.copy(
                selectedYearMonth = yearMonth,
                selectedYearMonthDisplay = formatMonthDisplay(currentCal),
            )
        }
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val logsFlow = workoutLogRepositoryPort?.getAllLogs() ?: flowOf(emptyList())
            val plansFlow = workoutPlanRepositoryPort?.getAllPlans() ?: flowOf(emptyList())

            combine(logsFlow, plansFlow) { logs, plans ->
                Pair(logs, plans)
            }.collect { (logs, plans) ->
                exerciseNameMap.clear()
                exerciseDayMap.clear()
                exerciseMuscleMap.clear()

                val activePlan = plans.firstOrNull { it.isActive } ?: plans.firstOrNull()
                if (activePlan != null) {
                    activePlanTitle = activePlan.title
                    activePlanTotalSessions = activePlan.totalSessions
                } else {
                    activePlanTitle = ""
                    activePlanTotalSessions = 12
                }

                workoutPlanRepositoryPort?.let { port ->
                    plans.forEach { plan ->
                        val planWithExercises = port.getPlanWithExercises(plan.id).firstOrNull()
                        planWithExercises?.second?.forEach { ex ->
                            exerciseNameMap[ex.id] = ex.name
                            exerciseDayMap[ex.id] = ex.day
                            exerciseMuscleMap[ex.id] = ex.muscleGroup
                        }
                    }
                }
                listLogs.clear()
                listLogs.addAll(logs)
                load()
            }
        }
    }

    fun selectPreviousMonth() {
        val cal = getSelectedCalendar()
        cal.add(Calendar.MONTH, -1)
        updateSelectedMonth(cal)
    }

    fun selectNextMonth() {
        val cal = getSelectedCalendar()
        cal.add(Calendar.MONTH, 1)
        updateSelectedMonth(cal)
    }

    private fun getSelectedCalendar(): Calendar {
        val cal = Calendar.getInstance()
        val parts = _uiState.value.selectedYearMonth.split("-")
        if (parts.size == 2) {
            val year = parts[0].toIntOrNull() ?: cal.get(Calendar.YEAR)
            val month = (parts[1].toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)) - 1
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, 1)
        }
        return cal
    }

    private fun updateSelectedMonth(cal: Calendar) {
        val yearMonth = String.format(Locale.ROOT, "%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        _uiState.update {
            it.copy(
                selectedYearMonth = yearMonth,
                selectedYearMonthDisplay = formatMonthDisplay(cal),
            )
        }
        load()
    }

    private fun formatMonthDisplay(cal: Calendar): String {
        val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.capitalizeLocale() ?: ""
        return "$monthName ${cal.get(Calendar.YEAR)}"
    }

    private fun String.capitalizeLocale(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    private fun load() {
        val mapped = listLogs.map { log -> sessionHistoryMap(log, exerciseNameMap) }
        val sessionClusterMap = sortLogs(listLogs)

        val groups =
            sessionClusterMap.map { (clusterKey, sessionLogs) ->
                sessionGroup(clusterKey, sessionLogs, exerciseNameMap, exerciseDayMap, exerciseMuscleMap)
            }

        val targetYearMonth = _uiState.value.selectedYearMonth
        val filteredGroups =
            groups.filter { group ->
                group.dateFormatted.startsWith(targetYearMonth)
            }

        val grid = buildCalendarGrid(targetYearMonth, groups.map { it.dateFormatted }.toSet())

        // Weekly metrics (current week or recent logs)
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000L)
        val weeklyLogs = listLogs.filter { it.timestamp >= weekAgo }
        val weeklyGroups = groups.filter { it.timestamp >= weekAgo }
        val weeklyVol = weeklyLogs.sumOf { it.repsCompleted * it.weightLiftedKg }
        val weeklyDurationSec = weeklyLogs.sumOf { it.durationSeconds }
        val weeklyHours = MathUtils.roundToDecimals(weeklyDurationSec / 3600.0, 1)

        // Active plan stats
        val totalPlanVolKg = listLogs.sumOf { it.repsCompleted * it.weightLiftedKg }
        val totalPlanVolTons = MathUtils.roundToDecimals(totalPlanVolKg / 1000.0, 1)
        val totalPlanDurationSec = listLogs.sumOf { it.durationSeconds }
        val totalPlanHours = MathUtils.roundToDecimals(totalPlanDurationSec / 3600.0, 1)
        val completedCount = groups.size
        val currentWeek = ((completedCount / 3) + 1).coerceAtMost(8)

        val activeStats =
            ActivePlanStatsUiModel(
                planTitle = activePlanTitle.ifBlank { "Plan de Entrenamiento" },
                currentWeek = currentWeek,
                totalWeeks = 8,
                completedSessionsCount = completedCount,
                totalSessionsGoal = activePlanTotalSessions,
                totalVolumeTons = totalPlanVolTons,
                totalHours = totalPlanHours,
            )

        // Global stats
        val globalVolFormatted = formatVolume(totalPlanVolKg)
        val globalStats =
            GlobalHistoryStatsUiModel(
                totalWorkoutsCount = groups.size,
                totalVolumeFormatted = globalVolFormatted,
                totalHours = totalPlanHours,
            )

        _uiState.update {
            it.copy(
                calendarGrid = grid,
                recordedSessions = mapped,
                sessionGroups = groups,
                filteredSessionGroups = if (filteredGroups.isNotEmpty()) filteredGroups else groups,
                weeklySessionsCount = weeklyGroups.size,
                weeklyTotalHours = weeklyHours,
                weeklyTotalVolumeKg = MathUtils.roundToDecimals(weeklyVol, 1),
                activePlanStats = activeStats,
                globalHistoryStats = globalStats,
                isLoading = false,
            )
        }
    }

    private fun formatVolume(kg: Double): String {
        return if (kg >= 1_000_000) {
            val millions = MathUtils.roundToDecimals(kg / 1_000_000.0, 2)
            "${millions}M kg"
        } else if (kg >= 1000) {
            val tons = MathUtils.roundToDecimals(kg / 1000.0, 1)
            "${tons}t"
        } else {
            "${kg.toInt()} kg"
        }
    }

    private fun buildCalendarGrid(
        yearMonth: String,
        workoutDates: Set<String>,
    ): List<CalendarDayUiModel> {
        val grid = mutableListOf<CalendarDayUiModel>()
        val cal = Calendar.getInstance()
        val parts = yearMonth.split("-")
        if (parts.size != 2) return emptyList()

        val year = parts[0].toIntOrNull() ?: cal.get(Calendar.YEAR)
        val month = (parts[1].toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)) - 1

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // Adjust for Monday start (1=Monday ... 7=Sunday)
        var firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (firstDayOfWeek == 0) firstDayOfWeek = 7

        // Previous month filler
        val prevCal = cal.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val prevDaysInMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (firstDayOfWeek - 1) downTo 1) {
            val dayNum = prevDaysInMonth - i + 1
            grid.add(
                CalendarDayUiModel(
                    dayNumber = dayNum,
                    isCurrentMonth = false,
                    isSelected = false,
                    hasWorkout = false,
                    dateIso = "",
                ),
            )
        }

        // Current month days
        for (day in 1..daysInMonth) {
            val dateIso = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day)
            val hasWorkout = workoutDates.contains(dateIso)
            grid.add(
                CalendarDayUiModel(
                    dayNumber = day,
                    isCurrentMonth = true,
                    isSelected = false,
                    hasWorkout = hasWorkout,
                    dateIso = dateIso,
                ),
            )
        }

        // Next month filler up to 35 or 42 cells
        val totalCells = if (grid.size > 35) 42 else 35
        var nextMonthDay = 1
        while (grid.size < totalCells) {
            grid.add(
                CalendarDayUiModel(
                    dayNumber = nextMonthDay++,
                    isCurrentMonth = false,
                    isSelected = false,
                    hasWorkout = false,
                    dateIso = "",
                ),
            )
        }

        return grid
    }

    private fun sessionHistoryMap(
        log: WorkoutLog,
        exerciseNameMap: Map<String, String>,
    ): SessionHistoryUiModel {
        val nameResolved = exerciseNameMap[log.exerciseId]?.takeIf { it.isNotBlank() }
        val fallbackName = "Ejercicio (${log.exerciseId.take(8)})"
        return SessionHistoryUiModel(
            id = log.id,
            exerciseId = log.exerciseId,
            exerciseName = nameResolved ?: fallbackName,
            repsCompleted = log.repsCompleted,
            weightLiftedKg = log.weightLiftedKg,
            heartRateBpm = log.heartRateBpm,
            sourceDevice = log.sourceDevice.name,
            timestamp = log.timestamp,
        )
    }

    private fun sortLogs(logs: List<WorkoutLog>): LinkedHashMap<String, MutableList<WorkoutLog>> {
        val sessionClusterMap = LinkedHashMap<String, MutableList<WorkoutLog>>()
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

    private fun sessionGroup(
        clusterKey: String,
        sessionLogs: MutableList<WorkoutLog>,
        exerciseNameMap: Map<String, String>,
        exerciseDayMap: Map<String, Int>,
        exerciseMuscleMap: Map<String, String>,
    ): WorkoutSessionGroupUiModel {
        val dateStr = DateTimeUtils.formatEpoch(sessionLogs.first().timestamp, "yyyy-MM-dd")

        val exerciseGroupMap = LinkedHashMap<String, MutableList<WorkoutLog>>()
        sessionLogs.forEach { log ->
            exerciseGroupMap.getOrPut(log.exerciseId) { mutableListOf() }.add(log)
        }

        var sessionDayNumber = 1
        val muscleGroupsSet = mutableSetOf<String>()

        val exerciseDetails =
            exerciseGroupMap.map { (exId, exLogs) ->
                val day = exerciseDayMap[exId] ?: 1
                if (day > 1) sessionDayNumber = day
                val muscle = exerciseMuscleMap[exId] ?: ""
                if (muscle.isNotBlank()) muscleGroupsSet.add(muscle)
                exerciseMap(exId, exLogs, exerciseNameMap, muscle)
            }

        val sessionVol = sessionLogs.sumOf { it.repsCompleted * it.weightLiftedKg }
        val sessionDurationSec = sessionLogs.sumOf { it.durationSeconds }
        val durationMins = (sessionDurationSec / 60L).toInt().coerceAtLeast(1)
        val dayTitle = "Día $sessionDayNumber"

        return WorkoutSessionGroupUiModel(
            sessionId = clusterKey,
            sessionTitle = dayTitle,
            dateFormatted = dateStr,
            durationMinutes = durationMins,
            timestamp = sessionLogs.first().timestamp,
            muscleGroups = muscleGroupsSet.toList(),
            totalExercisesCount = exerciseDetails.size,
            totalVolumeKg = MathUtils.roundToDecimals(sessionVol, 1),
            exercises = exerciseDetails,
        )
    }

    private fun exerciseMap(
        exId: String,
        exLogs: MutableList<WorkoutLog>,
        exerciseNameMap: Map<String, String>,
        muscleGroup: String,
    ): ExerciseSessionDetailUiModel {
        val exName = exerciseNameMap[exId]?.takeIf { it.isNotBlank() } ?: "Ejercicio (${exId.take(8)})"

        val sortedExLogs = exLogs.sortedBy { it.timestamp }
        val setModels =
            sortedExLogs.mapIndexed { index, l ->
                ExerciseLogSetUiModel(
                    setIndex = index + 1,
                    repsCompleted = l.repsCompleted,
                    weightLiftedKg = l.weightLiftedKg,
                    heartRateBpm = l.heartRateBpm,
                    durationSeconds = l.durationSeconds,
                    timestamp = l.timestamp,
                )
            }

        val avgReps = MathUtils.roundToDecimals(setModels.map { it.repsCompleted }.average(), 1)
        val avgWeight = MathUtils.roundToDecimals(setModels.map { it.weightLiftedKg }.average(), 1)

        return ExerciseSessionDetailUiModel(
            exerciseId = exId,
            exerciseName = exName,
            muscleGroup = muscleGroup,
            sets = setModels,
            averageReps = avgReps,
            averageWeightKg = avgWeight,
        )
    }
}
