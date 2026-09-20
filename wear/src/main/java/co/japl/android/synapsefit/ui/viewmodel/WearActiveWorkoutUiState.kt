package co.japl.android.synapsefit.ui.viewmodel

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState

data class WearActiveWorkoutUiState(
    val exerciseName: String = "",
    val currentHeartRateBpm: Int = 0,
    val currentReps: Int = 0,
    val currentWeight: Short = 0,
    val focusedInput: FocusedInput = FocusedInput.REPS,
    val isNumericKeypadOpen: Boolean = false,
    val isSyncedWithPhone: Boolean = true,
    val cooldownTargetTimestamp: Long? = null,
    val cooldownSecondsRemaining: Int? = null,
    val trainingStepState: TrainingStepState = TrainingStepState.ReadyForNext(null),
    val exerciseSessions: List<ExerciseSession> = emptyList(),
    val activeExerciseIndex: Int = 0,
    val activePlanTitle: String = "",
    val currentDay: Int = 1,
    val availableExercises: List<Exercise> = emptyList(),
    val isSessionStarted: Boolean = false,
    val isRoutineCompleted: Boolean = false,
    val workoutDurationSeconds: Long = 0L,
    val isPaused: Boolean = false,
    val isLiveSyncActive: Boolean = false,
    val activePlanDayId: String = "",
    val sessionStartTimestamp: Long = 0L,
    val activeExerciseId: String = "",
    val exerciseStartTimestamp: Long = 0L,
    val elapsedExerciseTimeFormatted: String = "",
)
