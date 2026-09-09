package co.japl.android.synapsefit.wear.ui.viewmodel

import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState

data class WearActiveWorkoutUiState(
    val exerciseName: String = "",
    val currentHeartRateBpm: Int = 0,
    val currentReps: Int = 0,
    val isSyncedWithPhone: Boolean = true,
    val cooldownTargetTimestamp: Long? = null,
    val cooldownSecondsRemaining: Int? = null,
    val trainingStepState: TrainingStepState = TrainingStepState.ReadyForNext(null),
    val exerciseSessions: List<ExerciseSession> = emptyList(),
    val activeExerciseIndex: Int = 0,
)
