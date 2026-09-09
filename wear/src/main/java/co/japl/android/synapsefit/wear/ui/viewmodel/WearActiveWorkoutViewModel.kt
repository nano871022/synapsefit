package co.japl.android.synapsefit.wear.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("TooManyFunctions", "LongMethod", "CyclomaticComplexMethod")
class WearActiveWorkoutViewModel(
    private val sensorPort: WearSensorPort? = null,
    private val syncPort: WearSyncPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
) : ViewModel() {
    private val _trainingStepState =
        MutableStateFlow<TrainingStepState>(TrainingStepState.ReadyForNext(null))
    val trainingStepState: StateFlow<TrainingStepState> = _trainingStepState.asStateFlow()

    private val _uiState = MutableStateFlow(WearActiveWorkoutUiState())
    val uiState: StateFlow<WearActiveWorkoutUiState> = _uiState.asStateFlow()

    fun loadExerciseSessions(sessions: List<ExerciseSession>) {
        if (sessions.isEmpty()) {
            _trainingStepState.value = TrainingStepState.ReadyForNext(null)
            _uiState.update {
                it.copy(
                    exerciseSessions = emptyList(),
                    trainingStepState = TrainingStepState.ReadyForNext(null),
                )
            }
            return
        }

        val firstIncomplete = sessions.firstOrNull { !it.isCompleted } ?: sessions.first()
        val firstIncompleteIndex = sessions.indexOf(firstIncomplete).coerceAtLeast(0)
        val initialSet = (firstIncomplete.completedSets + 1).coerceAtMost(firstIncomplete.targetSets)
        val initialState = TrainingStepState.Active(firstIncomplete, initialSet)

        _trainingStepState.value = initialState
        _uiState.update {
            it.copy(
                exerciseName = firstIncomplete.name,
                exerciseSessions = sessions,
                activeExerciseIndex = firstIncompleteIndex,
                trainingStepState = initialState,
            )
        }
    }

    fun completeSet() {
        val currentState = _trainingStepState.value
        if (currentState !is TrainingStepState.Active) return

        val session = currentState.exerciseSession
        val updatedCompletedSets = session.completedSets + 1
        val isExerciseDone = updatedCompletedSets >= session.targetSets

        val updatedSession =
            session.copy(
                completedSets = updatedCompletedSets,
                isCompleted = isExerciseDone,
            )

        val updatedList =
            _uiState.value.exerciseSessions.map {
                if (it.exerciseId == session.exerciseId) updatedSession else it
            }

        if (isExerciseDone) {
            val nextIncomplete = updatedList.firstOrNull { !it.isCompleted }
            val nextState = TrainingStepState.ReadyForNext(nextIncomplete)
            _trainingStepState.value = nextState

            _uiState.update {
                it.copy(
                    exerciseSessions = updatedList,
                    trainingStepState = nextState,
                    cooldownTargetTimestamp = null,
                    cooldownSecondsRemaining = null,
                )
            }
            syncPort?.queueDataForDeferredSync(
                exerciseId = session.exerciseId,
                reps = it.currentReps,
                heartRateBpm = it.currentHeartRateBpm,
            )
        } else {
            val targetTimestamp =
                DateTimeUtils.getCurrentTimestamp() + (session.restSeconds * 1000L)
            val remainingMillis = TrainingStepState.calculateRemainingMillis(targetTimestamp)
            val cooldownState =
                TrainingStepState.Cooldown(
                    exerciseSession = updatedSession,
                    targetTimestamp = targetTimestamp,
                    remainingMillis = remainingMillis,
                )
            _trainingStepState.value = cooldownState

            _uiState.update {
                it.copy(
                    exerciseSessions = updatedList,
                    trainingStepState = cooldownState,
                    cooldownTargetTimestamp = targetTimestamp,
                    cooldownSecondsRemaining = (remainingMillis / 1000L).toInt(),
                )
            }
        }
    }

    fun recalculateCooldownTimer() {
        val currentState = _trainingStepState.value
        if (currentState !is TrainingStepState.Cooldown) return

        val remainingMillis =
            TrainingStepState.calculateRemainingMillis(currentState.targetTimestamp)

        if (remainingMillis > 0L) {
            val updatedState =
                TrainingStepState.Cooldown(
                    exerciseSession = currentState.exerciseSession,
                    targetTimestamp = currentState.targetTimestamp,
                    remainingMillis = remainingMillis,
                )
            _trainingStepState.value = updatedState
            _uiState.update {
                it.copy(
                    trainingStepState = updatedState,
                    cooldownSecondsRemaining = (remainingMillis / 1000L).toInt(),
                )
            }
        } else {
            val nextIncomplete = _uiState.value.exerciseSessions.firstOrNull { !it.isCompleted }
            val nextState = TrainingStepState.ReadyForNext(nextIncomplete)
            _trainingStepState.value = nextState
            _uiState.update {
                it.copy(
                    trainingStepState = nextState,
                    cooldownTargetTimestamp = null,
                    cooldownSecondsRemaining = 0,
                )
            }
        }
    }

    fun startNextExercise() {
        val currentState = _trainingStepState.value
        if (currentState !is TrainingStepState.ReadyForNext) return

        val nextSession = currentState.nextExerciseSession ?: return
        val currentSet = (nextSession.completedSets + 1).coerceAtMost(nextSession.targetSets)
        val activeState = TrainingStepState.Active(nextSession, currentSet)

        _trainingStepState.value = activeState
        val nextIndex =
            _uiState.value.exerciseSessions.indexOfFirst {
                it.exerciseId == nextSession.exerciseId
            }.coerceAtLeast(0)

        _uiState.update {
            it.copy(
                exerciseName = nextSession.name,
                activeExerciseIndex = nextIndex,
                trainingStepState = activeState,
            )
        }
    }

    fun updateExerciseName(name: String) {
        _uiState.update { it.copy(exerciseName = name) }
    }

    fun updateHeartRate(bpm: Int) {
        sensorPort?.onHeartRateSensorChanged(bpm)
        _uiState.update { it.copy(currentHeartRateBpm = bpm) }
    }

    fun incrementReps() {
        _uiState.update { it.copy(currentReps = it.currentReps + 1) }
    }

    fun decrementReps() {
        _uiState.update { it.copy(currentReps = (it.currentReps - 1).coerceAtLeast(0)) }
    }

    fun setSyncStatus(isSynced: Boolean) {
        syncPort?.onConnectionStateChanged(isSynced)
        _uiState.update { it.copy(isSyncedWithPhone = isSynced) }
    }

    fun setCooldownTargetTimestamp(targetTimestamp: Long?) {
        _uiState.update {
            val remaining =
                if (targetTimestamp != null) {
                    val diff = targetTimestamp - DateTimeUtils.getCurrentTimestamp()
                    if (diff > 0) (diff / 1000L).toInt() else 0
                } else {
                    null
                }
            it.copy(
                cooldownTargetTimestamp = targetTimestamp,
                cooldownSecondsRemaining = remaining,
            )
        }
    }

    fun updateCooldownSeconds(seconds: Int?) {
        _uiState.update { it.copy(cooldownSecondsRemaining = seconds) }
    }
}
