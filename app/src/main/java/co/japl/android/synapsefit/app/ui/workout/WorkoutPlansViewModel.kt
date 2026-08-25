package co.japl.android.synapsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutPlanSummary(
    val id: String,
    val title: String,
    val goalDescription: String,
    val isActive: Boolean,
    val generatedByLlm: Boolean,
    val totalExercises: Int = 0,
)

data class WorkoutPlansUiState(
    val activePlan: WorkoutPlanSummary? = null,
    val archivedPlans: List<WorkoutPlanSummary> = emptyList(),
    val isLoading: Boolean = false,
)

class WorkoutPlansViewModel(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutPlansUiState())
    val uiState: StateFlow<WorkoutPlansUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appNavigator?.setLoading(true)

            workoutPlanRepositoryPort?.getAllPlans()?.collect { allPlans ->
                val summaries =
                    allPlans.map { plan ->
                        WorkoutPlanSummary(
                            id = plan.id,
                            title = plan.title,
                            goalDescription = plan.goalDescription,
                            isActive = plan.isActive,
                            generatedByLlm = plan.generatedByLlm,
                            totalExercises = 0,
                        )
                    }

                val active = summaries.find { it.isActive }
                val archived = summaries.filter { !it.isActive }

                _uiState.update {
                    it.copy(
                        activePlan = active,
                        archivedPlans = archived,
                        isLoading = false,
                    )
                }
                appNavigator?.setLoading(false)
            }
        }
    }
}
