package co.japl.android.synapsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MILLIS_PER_SECOND = 1000L

@Suppress("TooManyFunctions", "LongMethod", "CyclomaticComplexMethod", "MagicNumber")
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

    init {
        loadActivePlanData()
    }

    fun loadActivePlanData() {
        val repository = workoutPlanRepositoryPort ?: return
        viewModelScope.launch {
            repository.getActivePlan().collect { activePlan ->
                if (activePlan == null) return@collect
                val pair = repository.getPlanWithExercises(activePlan.id).firstOrNull()
                val exercises = pair?.second ?: emptyList()

                _uiState.update { current ->
                    current.copy(
                        activePlanTitle = activePlan.title,
                        currentDay = 1,
                        availableExercises = exercises,
                    )
                }

                if (_uiState.value.exerciseSessions.isEmpty() && exercises.isNotEmpty()) {
                    val sessions =
                        exercises.map { ex ->
                            ExerciseSession(
                                exerciseId = ex.id,
                                planId = ex.planId,
                                name = ex.name,
                                muscleGroup = ex.muscleGroup,
                                targetSets = ex.targetSets,
                                targetReps = ex.targetReps,
                                restSeconds = ex.restSeconds,
                            )
                        }
                    loadExerciseSessions(sessions)
                }
            }
        }
    }

    fun startSession() {
        if (_uiState.value.availableExercises.isNotEmpty() && _uiState.value.exerciseSessions.isEmpty()) {
            val sessions =
                _uiState.value.availableExercises.map { ex ->
                    ExerciseSession(
                        exerciseId = ex.id,
                        planId = ex.planId,
                        name = ex.name,
                        muscleGroup = ex.muscleGroup,
                        targetSets = ex.targetSets,
                        targetReps = ex.targetReps,
                        restSeconds = ex.restSeconds,
                    )
                }
            loadExerciseSessions(sessions)
        }
        _uiState.update { it.copy(isSessionStarted = true) }
    }

    fun selectExercise(
        exercise: Exercise,
        index: Int,
    ) {
        val sessions =
            if (_uiState.value.exerciseSessions.isEmpty()) {
                _uiState.value.availableExercises.map { ex ->
                    ExerciseSession(
                        exerciseId = ex.id,
                        planId = ex.planId,
                        name = ex.name,
                        muscleGroup = ex.muscleGroup,
                        targetSets = ex.targetSets,
                        targetReps = ex.targetReps,
                        restSeconds = ex.restSeconds,
                    )
                }
            } else {
                _uiState.value.exerciseSessions
            }

        val targetSession =
            if (sessions.isNotEmpty()) {
                sessions.getOrNull(index) ?: sessions.first()
            } else {
                ExerciseSession(
                    exerciseId = exercise.id,
                    planId = exercise.planId,
                    name = exercise.name,
                    muscleGroup = exercise.muscleGroup,
                    targetSets = exercise.targetSets,
                    targetReps = exercise.targetReps,
                    restSeconds = exercise.restSeconds,
                )
            }

        val updatedSessions =
            if (sessions.none { it.exerciseId == targetSession.exerciseId }) {
                sessions + targetSession
            } else {
                sessions
            }

        val initialSet = (targetSession.completedSets + 1).coerceAtMost(targetSession.targetSets)
        val initialState = TrainingStepState.Active(targetSession, initialSet)

        _trainingStepState.value = initialState
        _uiState.update {
            it.copy(
                exerciseName = targetSession.name,
                exerciseSessions = updatedSessions,
                activeExerciseIndex = index.coerceIn(0, maxOf(0, updatedSessions.lastIndex)),
                trainingStepState = initialState,
                isSessionStarted = true,
            )
        }
    }

    fun exitToSelectionHub() {
        _uiState.update { it.copy(isSessionStarted = false) }
    }

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

            _uiState.update { current ->
                current.copy(
                    exerciseSessions = updatedList,
                    trainingStepState = nextState,
                    cooldownTargetTimestamp = null,
                    cooldownSecondsRemaining = null,
                )
            }
            syncPort?.queueDataForDeferredSync(
                exerciseId = session.exerciseId,
                reps = _uiState.value.currentReps,
                heartRateBpm = _uiState.value.currentHeartRateBpm,
            )
        } else {
            val targetTimestamp =
                DateTimeUtils.getCurrentTimestamp() + (session.restSeconds * MILLIS_PER_SECOND)
            val remainingMillis = TrainingStepState.calculateRemainingMillis(targetTimestamp)
            val cooldownState =
                TrainingStepState.Cooldown(
                    exerciseSession = updatedSession,
                    targetTimestamp = targetTimestamp,
                    remainingMillis = remainingMillis,
                )
            _trainingStepState.value = cooldownState

            _uiState.update { current ->
                current.copy(
                    exerciseSessions = updatedList,
                    trainingStepState = cooldownState,
                    cooldownTargetTimestamp = targetTimestamp,
                    cooldownSecondsRemaining = (remainingMillis / MILLIS_PER_SECOND).toInt(),
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
                    cooldownSecondsRemaining = (remainingMillis / MILLIS_PER_SECOND).toInt(),
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
        val currentState = _trainingStepState.value
        if (currentState is TrainingStepState.Cooldown && targetTimestamp != null) {
            val remainingMillis = TrainingStepState.calculateRemainingMillis(targetTimestamp)
            _trainingStepState.value =
                TrainingStepState.Cooldown(
                    exerciseSession = currentState.exerciseSession,
                    targetTimestamp = targetTimestamp,
                    remainingMillis = remainingMillis,
                )
        }
        _uiState.update {
            val remaining =
                if (targetTimestamp != null) {
                    val diff = targetTimestamp - DateTimeUtils.getCurrentTimestamp()
                    if (diff > 0) (diff / MILLIS_PER_SECOND).toInt() else 0
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

    fun addExtraCooldownTime(extraSeconds: Int = 30) {
        val currentState = _trainingStepState.value
        val extraMillis = extraSeconds * MILLIS_PER_SECOND
        if (currentState is TrainingStepState.Cooldown) {
            val newTarget = currentState.targetTimestamp + extraMillis
            val newRemaining = TrainingStepState.calculateRemainingMillis(newTarget)
            val updatedState =
                TrainingStepState.Cooldown(
                    exerciseSession = currentState.exerciseSession,
                    targetTimestamp = newTarget,
                    remainingMillis = newRemaining,
                )
            _trainingStepState.value = updatedState
            _uiState.update {
                it.copy(
                    trainingStepState = updatedState,
                    cooldownTargetTimestamp = newTarget,
                    cooldownSecondsRemaining = (newRemaining / MILLIS_PER_SECOND).toInt(),
                )
            }
        }
    }

    fun skipCooldown() {
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
