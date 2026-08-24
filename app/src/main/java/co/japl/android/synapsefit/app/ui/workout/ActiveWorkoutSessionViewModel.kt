@file:Suppress("MagicNumber")

package co.japl.android.synapsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutSetUiModel(
    val setIndex: Int,
    val repsCompleted: String = "",
    val weightLiftedKg: String = "",
    val isCompleted: Boolean = false,
)

data class ActiveWorkoutUiState(
    val planId: String = "",
    val planTitle: String = "",
    val elapsedTimeSeconds: Long = 0L,
    val restTimerSecondsRemaining: Int? = null,
    val heartRateBpm: Int? = null,
    val currentExerciseName: String = "",
    val currentExerciseId: String = "",
    val exercises: List<ExerciseUiModel> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val sets: List<WorkoutSetUiModel> = emptyList(),
    val isSessionComplete: Boolean = false,
)

class ActiveWorkoutSessionViewModel(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val recordWorkoutSessionUseCase: RecordWorkoutSessionUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null

    fun startSession(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(planId = planId) }

            val planPair =
                workoutPlanRepositoryPort?.getPlanWithExercises(planId)?.let { flow ->
                    flow.firstOrNull()
                }

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
                        )
                    }

                val firstExercise = mappedExercises.firstOrNull()
                val initialSets =
                    (1..(firstExercise?.targetSets ?: 3)).map { idx ->
                        WorkoutSetUiModel(setIndex = idx, repsCompleted = firstExercise?.targetReps ?: "10")
                    }

                _uiState.update {
                    it.copy(
                        planTitle = plan.title,
                        exercises = mappedExercises,
                        currentExerciseIndex = 0,
                        currentExerciseId = firstExercise?.id ?: "",
                        currentExerciseName = firstExercise?.name ?: "",
                        sets = initialSets,
                    )
                }
            }

            startChronometer()
        }
    }

    private fun startChronometer() {
        timerJob?.cancel()
        timerJob =
            viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    _uiState.update { it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1) }
                }
            }
    }

    fun onSetRepsChange(
        setIndex: Int,
        reps: String,
    ) {
        _uiState.update { state ->
            val updated =
                state.sets.map { s ->
                    if (s.setIndex == setIndex) s.copy(repsCompleted = reps) else s
                }
            state.copy(sets = updated)
        }
    }

    fun onSetWeightChange(
        setIndex: Int,
        weight: String,
    ) {
        _uiState.update { state ->
            val updated =
                state.sets.map { s ->
                    if (s.setIndex == setIndex) s.copy(weightLiftedKg = weight) else s
                }
            state.copy(sets = updated)
        }
    }

    fun completeSet(setIndex: Int) {
        val state = _uiState.value
        val setModel = state.sets.find { it.setIndex == setIndex } ?: return
        val reps = setModel.repsCompleted.toIntOrNull() ?: 0
        val weight = setModel.weightLiftedKg.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            if (recordWorkoutSessionUseCase != null && state.currentExerciseId.isNotBlank() && reps > 0) {
                recordWorkoutSessionUseCase(
                    exerciseId = state.currentExerciseId,
                    repsCompleted = reps,
                    weightLiftedKg = weight,
                    heartRateBpm = state.heartRateBpm,
                    sourceDevice = SourceDevice.MOBILE,
                )
            }

            _uiState.update { s ->
                val updated =
                    s.sets.map { item ->
                        if (item.setIndex == setIndex) item.copy(isCompleted = true) else item
                    }
                s.copy(sets = updated)
            }

            val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)
            startRestTimer(currentExercise?.restSeconds ?: 60)
        }
    }

    private fun startRestTimer(restSeconds: Int) {
        restTimerJob?.cancel()
        restTimerJob =
            viewModelScope.launch {
                _uiState.update { it.copy(restTimerSecondsRemaining = restSeconds) }
                for (sec in restSeconds downTo 1) {
                    delay(1000L)
                    _uiState.update { it.copy(restTimerSecondsRemaining = sec - 1) }
                }
                _uiState.update { it.copy(restTimerSecondsRemaining = null) }
            }
    }

    fun updateHeartRateBpm(bpm: Int) {
        _uiState.update { it.copy(heartRateBpm = bpm) }
    }

    fun finishSession() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        _uiState.update { it.copy(isSessionComplete = true) }
    }
}
