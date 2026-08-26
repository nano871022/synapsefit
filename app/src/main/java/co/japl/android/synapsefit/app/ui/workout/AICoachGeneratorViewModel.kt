@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GenerateWorkoutPlanUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import co.japl.android.synapsefit.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AICoachGeneratorUiState(
    val selectedEnvironment: TrainingEnvironment = TrainingEnvironment.BODYWEIGHT,
    val gymChainQuery: String = "",
    val daysPerWeek: String = "4",
    val promptContext: String = "",
    val isGenerating: Boolean = false,
    val generationError: String? = null,
    val generatedPlan: WorkoutPlan? = null,
    val generatedExercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
)

class AICoachGeneratorViewModel(
    private val generateWorkoutPlanUseCase: GenerateWorkoutPlanUseCase? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val appNavigator: AppNavigator? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AICoachGeneratorUiState())
    val uiState: StateFlow<AICoachGeneratorUiState> = _uiState.asStateFlow()

    fun onEnvironmentSelected(env: TrainingEnvironment) {
        _uiState.update { it.copy(selectedEnvironment = env, generationError = null) }
    }

    fun onGymChainQueryChange(query: String) {
        _uiState.update { it.copy(gymChainQuery = query) }
    }

    fun onDaysPerWeekChange(days: String) {
        _uiState.update { it.copy(daysPerWeek = days) }
    }

    fun onPromptContextChange(context: String) {
        _uiState.update { it.copy(promptContext = context) }
    }

    fun generatePlan() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, generationError = null) }
            appNavigator?.setLoading(true)

            if (generateWorkoutPlanUseCase != null) {
                val daysInt = state.daysPerWeek.toIntOrNull()
                val result =
                    generateWorkoutPlanUseCase(
                        promptContext = state.promptContext.ifBlank { "Plan de entrenamiento general de hipertrofia y fuerza" },
                        environment = state.selectedEnvironment,
                        gymChainQuery = if (state.selectedEnvironment == TrainingEnvironment.CHAIN_GYM) state.gymChainQuery else null,
                        daysPerWeek = daysInt,
                    )
                result.fold(
                    onSuccess = { (plan, exercises) ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                generatedPlan = plan,
                                generatedExercises = exercises,
                            )
                        }
                        appNavigator?.setLoading(false)
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                generationError = error.message ?: "Error al generar la rutina con IA",
                            )
                        }
                        appNavigator?.setLoading(false)
                    },
                )
            } else {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        generationError = "Configuración LLM no disponible",
                    )
                }
                appNavigator?.setLoading(false)
            }
        }
    }

    fun acceptPlan() {
        val planId = _uiState.value.generatedPlan?.id ?: return
        viewModelScope.launch {
            workoutPlanRepositoryPort?.setActivePlan(planId)
            appNavigator?.navigateTo(Routes.DASHBOARD, popUpToRoute = Routes.WORKOUT_PLANS, inclusive = true)
        }
    }

    fun discardPlan() {
        val planId = _uiState.value.generatedPlan?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            workoutPlanRepositoryPort?.deletePlan(planId)
            _uiState.update {
                it.copy(
                    generatedPlan = null,
                    generatedExercises = emptyList(),
                    isLoading = false,
                )
            }
        }
    }
}
