package co.japl.android.synapsefit.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                        if (activePlan != null) {
                            _uiState.update {
                                it.copy(
                                    todayWorkoutTitle = activePlan.title,
                                    todayWorkoutPlanId = activePlan.id,
                                    isLoading = false,
                                )
                            }
                        } else {
                            // If no active plan, show the latest created one as a suggestion
                            workoutPlanRepositoryPort.getAllPlans().collect { allPlans ->
                                val latestPlan = allPlans.maxByOrNull { it.updatedAt }
                                _uiState.update {
                                    it.copy(
                                        todayWorkoutTitle = latestPlan?.title ?: "Sin rutina activa",
                                        todayWorkoutPlanId = latestPlan?.id,
                                        isLoading = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}
