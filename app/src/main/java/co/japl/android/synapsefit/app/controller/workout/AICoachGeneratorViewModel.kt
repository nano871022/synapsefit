@file:Suppress("MaxLineLength", "LongParameterList")

package co.japl.android.synapsefit.app.controller.workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GenerateWorkoutPlanUseCase
import co.japl.android.synapsefit.core.usecase.GetExerciseMediaUseCase
import co.japl.android.synapsefit.core.usecase.OptimizeWorkoutPromptUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import co.japl.android.synapsefit.navigation.Routes
import co.japl.android.synapsefit.service.SynapseFitForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AICoachGeneratorUiState(
    val selectedLocation: TrainingLocation = TrainingLocation.HOME,
    val selectedEquipment: EquipmentPreference = EquipmentPreference.NO_PREFERENCE,
    val gymChainQuery: String = "",
    val daysPerWeek: String = "4",
    val promptContext: String = "",
    val isGenerating: Boolean = false,
    val isOptimizing: Boolean = false,
    val generationError: String? = null,
    val generatedPlan: WorkoutPlan? = null,
    val generatedExercises: List<Exercise> = emptyList(),
    val isFetchingMedia: Boolean = false,
    val mediaProgress: Float = 0f,
    val isLoading: Boolean = false,
)

class AICoachGeneratorViewModel(
    private val generateWorkoutPlanUseCase: GenerateWorkoutPlanUseCase? = null,
    private val optimizeWorkoutPromptUseCase: OptimizeWorkoutPromptUseCase? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val getExerciseMediaUseCase: GetExerciseMediaUseCase? = null,
    private val appNavigator: AppNavigator? = null,
    private val context: Context? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AICoachGeneratorUiState())
    val uiState: StateFlow<AICoachGeneratorUiState> = _uiState.asStateFlow()

    fun onGymChainQueryChange(query: String) {
        _uiState.update { it.copy(gymChainQuery = query) }
    }

    fun onLocationSelected(loc: TrainingLocation) {
        _uiState.update { it.copy(selectedLocation = loc, generationError = null) }
    }

    fun onEquipmentSelected(equip: EquipmentPreference) {
        _uiState.update { it.copy(selectedEquipment = equip) }
    }

    fun optimizePrompt() {
        val state = _uiState.value
        if (state.promptContext.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizing = true, generationError = null) }
            context?.let { SynapseFitForegroundService.startLlmService(it, "Optimizando solicitud") }

            val result = optimizeWorkoutPromptUseCase?.invoke(
                userPrompt = state.promptContext,
                location = state.selectedLocation,
                equipment = state.selectedEquipment
            )

            result?.fold(
                onSuccess = { optimized ->
                    _uiState.update { it.copy(promptContext = optimized, isOptimizing = false) }
                    context?.let { SynapseFitForegroundService.stopService(it) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isOptimizing = false, generationError = "Error de seguridad u optimización: ${error.message}") }
                    context?.let { SynapseFitForegroundService.stopService(it) }
                }
            )
        }
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
            context?.let { SynapseFitForegroundService.startLlmService(it, "Generando plan de entrenamiento con IA") }

            if (generateWorkoutPlanUseCase != null) {
                val daysInt = state.daysPerWeek.toIntOrNull()
                val result =
                    generateWorkoutPlanUseCase(
                        promptContext = state.promptContext.ifBlank { "Plan de entrenamiento general de hipertrofia y fuerza" },
                        location = state.selectedLocation,
                        equipment = state.selectedEquipment,
                        gymChainQuery = if (state.selectedLocation == co.japl.android.synapsefit.core.domain.model.TrainingLocation.GYM) state.gymChainQuery else null,
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
                        context?.let { SynapseFitForegroundService.stopService(it) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                generationError = error.message ?: "Error al generar la rutina con IA",
                            )
                        }
                        appNavigator?.setLoading(false)
                        context?.let { SynapseFitForegroundService.stopService(it) }
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
                context?.let { SynapseFitForegroundService.stopService(it) }
            }
        }
    }

    fun acceptPlan() {
        val planId = _uiState.value.generatedPlan?.id ?: return
        val exercises = _uiState.value.generatedExercises
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingMedia = true, mediaProgress = 0f) }
            context?.let { SynapseFitForegroundService.startLlmService(it, "Obteniendo recursos multimedia") }
            workoutPlanRepositoryPort?.setActivePlan(planId)

            if (getExerciseMediaUseCase != null && exercises.isNotEmpty()) {
                exercises.forEachIndexed { index, ex ->
                    runCatching {
                        getExerciseMediaUseCase(
                            exerciseId = ex.id,
                            exerciseName = ex.name,
                            guideVideoUrl = ex.guideVideoUrl,
                            guideImageUrl = ex.guideImageUrl,
                        )
                    }
                    val progress = (index + 1).toFloat() / exercises.size
                    _uiState.update { it.copy(mediaProgress = progress) }
                }
            }

            _uiState.update { it.copy(isFetchingMedia = false) }
            context?.let { SynapseFitForegroundService.stopService(it) }
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
