@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.app.controller.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userName: String = "Atleta SynapseFit",
    val profileImageUrl: String? = null,
    val latestWeightKg: Double? = null,
    val weightTrendDeltaKg: Double? = null,
    val todayWorkoutTitle: String? = null,
    val todayWorkoutPlanId: String? = null,
    val isSyncing: Boolean = false,
    val isLoading: Boolean = false,
)

class DashboardViewModel(
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var dashboardJob: kotlinx.coroutines.Job? = null

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        dashboardJob?.cancel()
        dashboardJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                launch {
                    bodyMeasurementRepositoryPort?.getMeasurementsHistory()?.collect { measurements ->
                        val latestMeasurement = measurements.maxByOrNull { it.createdAt }
                        val sorted = measurements.sortedByDescending { it.createdAt }
                        val secondLatestMeasurement = sorted.getOrNull(1)

                        val latestWeight = latestMeasurement?.weightKg
                        val deltaWeight =
                            if (latestMeasurement != null && secondLatestMeasurement != null) {
                                latestMeasurement.weightKg - secondLatestMeasurement.weightKg
                            } else {
                                null
                            }

                        _uiState.update {
                            it.copy(
                                latestWeightKg = latestWeight,
                                weightTrendDeltaKg = deltaWeight,
                                isLoading = false,
                            )
                        }
                    }
                }

                launch {
                    workoutPlanRepositoryPort?.getActivePlan()?.collect { activePlan ->
                        val targetPlan = activePlan ?: workoutPlanRepositoryPort.getAllPlans().firstOrNull()?.maxByOrNull { it.updatedAt }
                        if (targetPlan != null) {
                            val planPair = workoutPlanRepositoryPort.getPlanWithExercises(targetPlan.id).firstOrNull()
                            val exercises = planPair?.second ?: emptyList()
                            val totalPlanDays = exercises.maxOfOrNull { it.day } ?: 1

                            val latestLogs = workoutLogRepositoryPort?.getLatestLogsForPlan(targetPlan.id)?.firstOrNull() ?: emptyList()
                            val lastLog = latestLogs.firstOrNull()
                            val lastEx = exercises.find { it.id == lastLog?.exerciseId }
                            val lastDay = lastEx?.day ?: 0

                            val activeDayNumber = if (lastDay == 0) 1 else (lastDay % totalPlanDays) + 1

                            _uiState.update {
                                it.copy(
                                    todayWorkoutTitle = "Día $activeDayNumber - ${targetPlan.title}",
                                    todayWorkoutPlanId = targetPlan.id,
                                    isLoading = false,
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    todayWorkoutTitle = "Sin rutina activa",
                                    todayWorkoutPlanId = null,
                                    isLoading = false,
                                )
                            }
                        }
                    }
                }
            }
    }
}
