package co.japl.android.synapsefit.app.controller.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseUiModel(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val targetSets: Int,
    val targetReps: String,
    val restSeconds: Int,
    val day: Int = 1,
    val guideVideoUrl: String? = null,
    val guideImageUrl: String? = null,
)

data class WorkoutPlanDetailUiState(
    val planId: String = "",
    val planTitle: String = "",
    val goalDescription: String = "",
    val daySubtitle: String = "",
    val totalExercises: Int = 0,
    val exercises: List<ExerciseUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

class WorkoutPlanDetailViewModel(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutPlanDetailUiState())
    val uiState: StateFlow<WorkoutPlanDetailUiState> = _uiState.asStateFlow()

    fun loadPlanDetail(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, planId = planId) }

            workoutPlanRepositoryPort?.getPlanWithExercises(planId)?.collect { planPair ->
                if (planPair != null) {
                    val (plan, exercises) = planPair
                    val mappedExercises =
                        exercises.map { e ->
                            ExerciseUiModel(
                                id = e.id,
                                name = e.name,
                                muscleGroup = e.muscleGroup,
                                targetSets = e.targetSets,
                                targetReps = e.targetReps,
                                restSeconds = e.restSeconds,
                                day = e.day,
                                guideVideoUrl = e.guideVideoUrl,
                                guideImageUrl = e.guideImageUrl,
                            )
                        }
                    _uiState.update {
                        it.copy(
                            planTitle = plan.title,
                            goalDescription = plan.goalDescription,
                            totalExercises = exercises.size,
                            exercises = mappedExercises,
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
