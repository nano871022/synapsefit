package co.japl.android.synapsefit.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val measurements =
                bodyMeasurementRepositoryPort?.getMeasurementsHistory()?.let { flow ->
                    flow.firstOrNull()
                } ?: emptyList()

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

            val activePlan =
                workoutPlanRepositoryPort?.getActivePlan()?.let { flow ->
                    flow.firstOrNull()
                }

            _uiState.update {
                it.copy(
                    latestWeightKg = latestWeight,
                    weightTrendDeltaKg = deltaWeight,
                    todayWorkoutTitle = activePlan?.title ?: "Sin rutina activa",
                    todayWorkoutPlanId = activePlan?.id,
                    isLoading = false,
                )
            }
        }
    }
}
