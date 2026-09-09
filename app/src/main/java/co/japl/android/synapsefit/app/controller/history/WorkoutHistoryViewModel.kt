@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod", "CyclomaticComplexMethod", "TooGenericExceptionCaught")

package co.japl.android.synapsefit.app.controller.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import co.japl.android.synapsefit.util.MathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

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

            val historyFlow = getGroupedWorkoutHistoryUseCase?.invoke() ?: flowOf(emptyList())
            val plansFlow = workoutPlanRepositoryPort?.getAllPlans() ?: flowOf(emptyList())

            combine(historyFlow, plansFlow) { groups, plans ->
                val activePlan = plans.firstOrNull { it.isActive } ?: plans.firstOrNull()
                val activePlanTitle = activePlan?.title ?: ""
                val activePlanTotalSessions = activePlan?.totalSessions ?: 12

                Triple(groups, activePlanTitle, activePlanTotalSessions)
            }.collect { (groups, activePlanTitle, activePlanTotalSessions) ->
                updateStateWithGroups(groups, activePlanTitle, activePlanTotalSessions)
            }
        }
    }

    private fun updateStateWithGroups(
        groups: List<WorkoutHistoryGroup>,
        activePlanTitle: String,
        activePlanTotalSessions: Int,
    ) {
        val uiGroups =
            groups.map { group ->
                WorkoutSessionGroupUiModel(
                    sessionId = group.sessionId,
                    sessionTitle = "Día ${group.day}",
                    dateFormatted = dateFormatter.format(Instant.ofEpochMilli(group.timestamp)),
                    durationMinutes = (group.totalDurationSeconds / 60).toInt().coerceAtLeast(1),
                    timestamp = group.timestamp,
                    muscleGroups = group.muscleGroups,
                    totalExercisesCount = group.exercises.size,
                    totalVolumeKg = MathUtils.roundToDecimals(group.totalVolumeKg, 1),
                    exercises =
                        group.exercises.map { ex ->
                            ExerciseSessionDetailUiModel(
                                exerciseId = ex.exerciseId,
                                exerciseName = ex.exerciseName,
                                muscleGroup = ex.muscleGroup,
                                sets =
                                    ex.sets.map { s ->
                                        ExerciseLogSetUiModel(
                                            setIndex = s.setIndex,
                                            repsCompleted = s.repsCompleted,
                                            weightLiftedKg = s.weightLiftedKg,
                                            heartRateBpm = s.heartRateBpm,
                                            durationSeconds = s.durationSeconds,
                                            timestamp = s.timestamp,
                                        )
                                    },
                                averageReps = ex.averageReps,
                                averageWeightKg = ex.averageWeightKg,
                            )
                        },
                )
            }

        val targetYearMonth = _uiState.value.selectedYearMonth
        val filteredGroups = uiGroups.filter { it.dateFormatted.startsWith(targetYearMonth) }
        val grid = buildCalendarGrid(targetYearMonth, uiGroups.map { it.dateFormatted }.toSet())

        // Weekly metrics
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000L)
        val weeklyGroups = uiGroups.filter { it.timestamp >= weekAgo }
        val weeklyVol = weeklyGroups.sumOf { it.totalVolumeKg }
        val weeklyDurationSec = weeklyGroups.sumOf { it.durationMinutes * 60L }
        val weeklyHours = MathUtils.roundToDecimals(weeklyDurationSec / 3600.0, 1)

        // Active plan stats
        val totalPlanVolKg = uiGroups.sumOf { it.totalVolumeKg }
        val totalPlanVolTons = MathUtils.roundToDecimals(totalPlanVolKg / 1000.0, 1)
        val totalPlanDurationSec = uiGroups.sumOf { it.durationMinutes * 60L }
        val totalPlanHours = MathUtils.roundToDecimals(totalPlanDurationSec / 3600.0, 1)
        val completedCount = uiGroups.size
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

        val globalStats =
            GlobalHistoryStatsUiModel(
                totalWorkoutsCount = uiGroups.size,
                totalVolumeFormatted = formatVolume(totalPlanVolKg),
                totalHours = totalPlanHours,
            )

        _uiState.update {
            it.copy(
                calendarGrid = grid,
                sessionGroups = uiGroups,
                filteredSessionGroups = if (filteredGroups.isNotEmpty()) filteredGroups else uiGroups,
                weeklySessionsCount = weeklyGroups.size,
                weeklyTotalHours = weeklyHours,
                weeklyTotalVolumeKg = MathUtils.roundToDecimals(weeklyVol, 1),
                activePlanStats = activeStats,
                globalHistoryStats = globalStats,
                isLoading = false,
            )
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
        loadHistory()
    }

    private fun formatMonthDisplay(cal: Calendar): String {
        val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.capitalizeLocale() ?: ""
        return "$monthName ${cal.get(Calendar.YEAR)}"
    }

    private fun String.capitalizeLocale(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

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
        var firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (firstDayOfWeek == 0) firstDayOfWeek = 7

        val prevCal = cal.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val prevDaysInMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (firstDayOfWeek - 1) downTo 1) {
            val dayNum = prevDaysInMonth - i + 1
            grid.add(CalendarDayUiModel(dayNum, false, false, false, ""))
        }

        for (day in 1..daysInMonth) {
            val dateIso = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day)
            val hasWorkout = workoutDates.contains(dateIso)
            grid.add(CalendarDayUiModel(day, true, false, hasWorkout, dateIso))
        }

        val totalCells = if (grid.size > 35) 42 else 35
        var nextMonthDay = 1
        while (grid.size < totalCells) {
            grid.add(CalendarDayUiModel(nextMonthDay++, false, false, false, ""))
        }
        return grid
    }
}
