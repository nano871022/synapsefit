package co.japl.android.synapsefit.ui.viewmodel

import android.view.KeyEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.util.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val MILLIS_PER_SECOND = 1000L
private const val DEFAULT_REST_SECONDS = 60L
private const val ROTARY_THRESHOLD = 0.5f

@Suppress("TooManyFunctions", "LongMethod", "CyclomaticComplexMethod", "MagicNumber", "LargeClass")
class WearActiveWorkoutViewModel(
    private val sensorPort: WearSensorPort? = null,
    private val syncPort: WearSyncPort? = null,
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val wearStateMirrorPort: WearStateMirrorPort? = null,
) : ViewModel() {
    private val _trainingStepState =
        MutableStateFlow<TrainingStepState>(TrainingStepState.ReadyForNext(null))
    val trainingStepState: StateFlow<TrainingStepState> = _trainingStepState.asStateFlow()

    private val _uiState = MutableStateFlow(WearActiveWorkoutUiState())
    val uiState: StateFlow<WearActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var rotaryAccumulator = 0f

    init {
        loadActivePlanData()
        observeSensorPort()
        observeMirrorPort()
    }

    private fun observeMirrorPort() {
        val mirror = wearStateMirrorPort ?: return
        viewModelScope.launch {
            mirror.isConnected.collect { connected ->
                if (connected) {
                    _uiState.update { it.copy(isLiveSyncActive = true) }
                }
            }
        }
        viewModelScope.launch {
            mirror.liveSyncEvents.collect { event ->
                _uiState.update { it.copy(isLiveSyncActive = true) }
                handleIncomingLiveSyncEvent(event)
            }
        }
    }

    private fun handleIncomingLiveSyncEvent(event: LiveSyncEvent) {
        when (event) {
            is LiveSyncEvent.PingSession -> handlePingSessionEvent()
            is LiveSyncEvent.ActiveSessionStatePayload -> handleActiveSessionStatePayload(event)
            else -> {}
        }
    }

    private fun handlePingSessionEvent() {
        val state = _uiState.value
        if (state.isSessionStarted && state.exerciseSessions.isNotEmpty()) {
            val currentExId =
                state.exerciseSessions.getOrNull(state.activeExerciseIndex)?.exerciseId ?: ""
            val completedIds =
                state.exerciseSessions.filter { it.isCompleted }.map { it.exerciseId }
            viewModelScope.launch {
                wearStateMirrorPort?.sendEvent(
                    LiveSyncEvent.ActiveSessionStatePayload(
                        isLiveActive = true,
                        planId = state.activePlanTitle,
                        day = state.currentDay,
                        currentExerciseId = currentExId,
                        activeSet = 1,
                        cooldownTargetTimestamp = state.cooldownTargetTimestamp,
                        completedExerciseIds = completedIds,
                    ),
                )
            }
        }
    }

    private fun handleActiveSessionStatePayload(event: LiveSyncEvent.ActiveSessionStatePayload) {
        if (!event.isLiveActive) return

        val state = _uiState.value
        val updatedSessions =
            state.exerciseSessions.map { session ->
                if (event.completedExerciseIds.contains(session.exerciseId)) {
                    session.copy(isCompleted = true, completedSets = session.targetSets)
                } else {
                    session
                }
            }
        val targetIndex =
            updatedSessions.indexOfFirst { it.exerciseId == event.currentExerciseId }
                .coerceAtLeast(0)
        val activeSession = updatedSessions.getOrNull(targetIndex)

        val cdTarget = event.cooldownTargetTimestamp
        val nextState =
            if (cdTarget != null) {
                val remaining =
                    DateTimeUtils.getCurrentTimestamp().let { now ->
                        val diff = cdTarget - now
                        if (diff > 0) diff else 0L
                    }
                if (activeSession != null) {
                    TrainingStepState.Cooldown(activeSession, cdTarget, remaining)
                } else {
                    _trainingStepState.value
                }
            } else if (activeSession != null) {
                TrainingStepState.Active(activeSession, event.activeSet)
            } else {
                _trainingStepState.value
            }

        _trainingStepState.value = nextState
        _uiState.update {
            it.copy(
                isLiveSyncActive = true,
                isSessionStarted = true,
                exerciseSessions = updatedSessions,
                activeExerciseIndex = targetIndex,
                exerciseName = activeSession?.name ?: it.exerciseName,
                cooldownTargetTimestamp = event.cooldownTargetTimestamp,
                trainingStepState = nextState,
            )
        }
    }

    private fun observeSensorPort() {
        val sensor = sensorPort ?: return
        sensor.startHeartRateMonitoring()
        viewModelScope.launch {
            sensor.heartRateBpm.collect { bpm ->
                if (bpm > 0) {
                    _uiState.update { it.copy(currentHeartRateBpm = bpm) }
                }
            }
        }
    }

    private fun startWorkoutTimer() {
        if (timerJob?.isActive == true) return
        timerJob =
            viewModelScope.launch {
                while (true) {
                    delay(MILLIS_PER_SECOND)
                    if (!_uiState.value.isPaused && _uiState.value.isSessionStarted) {
                        _uiState.update { it.copy(workoutDurationSeconds = it.workoutDurationSeconds + 1) }
                    }
                }
            }
    }

    fun togglePauseResume() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun startSession() {
        _uiState.update { it.copy(isSessionStarted = true) }
        startWorkoutTimer()
    }

    fun setFocusedInput(input: FocusedInput) {
        _uiState.update { it.copy(focusedInput = input) }
    }

    fun openNumericKeypad() {
        _uiState.update { it.copy(isNumericKeypadOpen = true) }
    }

    fun closeNumericKeypad() {
        _uiState.update { it.copy(isNumericKeypadOpen = false) }
    }

    fun setFocusedValueDirect(value: Int) {
        val safeVal = value.coerceAtLeast(0)
        _uiState.update { state ->
            when (state.focusedInput) {
                FocusedInput.REPS -> state.copy(currentReps = safeVal, isNumericKeypadOpen = false)
                FocusedInput.WEIGHT -> state.copy(currentWeight = safeVal.toShort(), isNumericKeypadOpen = false)
            }
        }
    }

    fun incrementFocusedInput() {
        when (_uiState.value.focusedInput) {
            FocusedInput.REPS -> incrementReps()
            FocusedInput.WEIGHT -> incrementWgt()
        }
    }

    fun decrementFocusedInput() {
        when (_uiState.value.focusedInput) {
            FocusedInput.REPS -> decrementReps()
            FocusedInput.WEIGHT -> decrementWgt()
        }
    }

    fun handleRotaryScroll(delta: Float) {
        rotaryAccumulator += delta
        if (abs(rotaryAccumulator) >= ROTARY_THRESHOLD) {
            if (rotaryAccumulator > 0) {
                incrementFocusedInput()
            } else {
                decrementFocusedInput()
            }
            rotaryAccumulator = 0f
        }
    }

    fun handleHardwareKey(keyCode: Int): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_STEM_1 -> {
                incrementFocusedInput()
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_STEM_2 -> {
                decrementFocusedInput()
                true
            }
            else -> false
        }
    }

    fun loadPlanData(
        planId: String,
        day: Int,
    ) {
        val repository = workoutPlanRepositoryPort ?: return
        viewModelScope.launch {
            val targetPlanId =
                if (planId.isBlank() || planId == "default") {
                    repository.getActivePlan().firstOrNull()?.id ?: ""
                } else {
                    planId
                }
            if (targetPlanId.isBlank()) return@launch

            val pair = repository.getPlanWithExercises(targetPlanId).firstOrNull()
            val plan = pair?.first
            val allExercises = pair?.second ?: emptyList()
            val exercises =
                if (day > 0) {
                    val filtered = allExercises.filter { it.day == day }
                    if (filtered.isNotEmpty()) filtered else allExercises
                } else {
                    allExercises
                }

            _uiState.update { current ->
                current.copy(
                    activePlanTitle = plan?.title ?: current.activePlanTitle,
                    currentDay = if (day > 0) day else current.currentDay,
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

    fun selectExercise(
        exercise: Exercise,
        index: Int,
    ) {
        val sessions = _uiState.value.exerciseSessions
        val targetSession =
            sessions.firstOrNull { it.exerciseId == exercise.id } ?: run {
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
                isRoutineCompleted = false,
            )
        }
        startWorkoutTimer()
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
                    isRoutineCompleted = false,
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
                isRoutineCompleted = false,
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

        syncPort?.queueDataForDeferredSync(
            exerciseId = session.exerciseId,
            reps = _uiState.value.currentReps,
            heartRateBpm = _uiState.value.currentHeartRateBpm,
        )

        val targetTimestamp =
            DateTimeUtils.getCurrentTimestamp() + (DEFAULT_REST_SECONDS * MILLIS_PER_SECOND)
        val remainingMillis = TrainingStepState.calculateRemainingMillis(targetTimestamp)

        val allRoutineDone = updatedList.isNotEmpty() && updatedList.all { it.isCompleted }

        if (isExerciseDone) {
            val nextIncomplete = updatedList.firstOrNull { !it.isCompleted }
            val nextIncompleteIndex =
                if (nextIncomplete != null) {
                    updatedList.indexOfFirst {
                        it.exerciseId == nextIncomplete.exerciseId
                    }.coerceAtLeast(0)
                } else {
                    _uiState.value.activeExerciseIndex
                }

            if (nextIncomplete != null) {
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
                        activeExerciseIndex = nextIncompleteIndex,
                        exerciseName = nextIncomplete.name,
                        trainingStepState = cooldownState,
                        cooldownTargetTimestamp = targetTimestamp,
                        cooldownSecondsRemaining = (remainingMillis / MILLIS_PER_SECOND).toInt(),
                    )
                }
            } else {
                val nextState = TrainingStepState.ReadyForNext(null)
                _trainingStepState.value = nextState

                _uiState.update { current ->
                    current.copy(
                        exerciseSessions = updatedList,
                        trainingStepState = nextState,
                        cooldownTargetTimestamp = null,
                        cooldownSecondsRemaining = null,
                        isRoutineCompleted = allRoutineDone,
                    )
                }
            }
        } else {
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
            val nextIndex =
                if (nextIncomplete != null) {
                    _uiState.value.exerciseSessions.indexOfFirst {
                        it.exerciseId == nextIncomplete.exerciseId
                    }.coerceAtLeast(0)
                } else {
                    _uiState.value.activeExerciseIndex
                }
            val nextState = TrainingStepState.ReadyForNext(nextIncomplete)
            val allRoutineDone =
                _uiState.value.exerciseSessions.isNotEmpty() &&
                    _uiState.value.exerciseSessions.all { it.isCompleted }

            _trainingStepState.value = nextState
            _uiState.update {
                it.copy(
                    activeExerciseIndex = nextIndex,
                    exerciseName = nextIncomplete?.name ?: it.exerciseName,
                    trainingStepState = nextState,
                    cooldownTargetTimestamp = null,
                    cooldownSecondsRemaining = 0,
                    isRoutineCompleted = allRoutineDone,
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

    fun navigateToNextExercise() {
        val sessions = _uiState.value.exerciseSessions
        if (sessions.isEmpty()) return
        val nextIndex = (_uiState.value.activeExerciseIndex + 1) % sessions.size
        val nextSession = sessions[nextIndex]
        val currentSet = (nextSession.completedSets + 1).coerceAtMost(nextSession.targetSets)
        val activeState = TrainingStepState.Active(nextSession, currentSet)
        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                exerciseName = nextSession.name,
                activeExerciseIndex = nextIndex,
                trainingStepState = activeState,
            )
        }
    }

    fun navigateToPreviousExercise() {
        val sessions = _uiState.value.exerciseSessions
        if (sessions.isEmpty()) return
        val prevIndex =
            if (_uiState.value.activeExerciseIndex - 1 < 0) {
                sessions.lastIndex
            } else {
                _uiState.value.activeExerciseIndex - 1
            }
        val prevSession = sessions[prevIndex]
        val currentSet = (prevSession.completedSets + 1).coerceAtMost(prevSession.targetSets)
        val activeState = TrainingStepState.Active(prevSession, currentSet)
        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                exerciseName = prevSession.name,
                activeExerciseIndex = prevIndex,
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

    fun incrementWgt() {
        _uiState.update { it.copy(currentWeight = (it.currentWeight + 1.toShort()).toShort()) }
    }

    fun decrementWgt() {
        _uiState.update {
            it.copy(
                currentWeight =
                    (it.currentWeight - 1.toShort()).coerceAtLeast(0)
                        .toShort(),
            )
        }
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
        val nextIndex =
            if (nextIncomplete != null) {
                _uiState.value.exerciseSessions.indexOfFirst {
                    it.exerciseId == nextIncomplete.exerciseId
                }.coerceAtLeast(0)
            } else {
                _uiState.value.activeExerciseIndex
            }
        val nextState = TrainingStepState.ReadyForNext(nextIncomplete)
        val allRoutineDone =
            _uiState.value.exerciseSessions.isNotEmpty() &&
                _uiState.value.exerciseSessions.all { it.isCompleted }

        _trainingStepState.value = nextState
        _uiState.update {
            it.copy(
                activeExerciseIndex = nextIndex,
                exerciseName = nextIncomplete?.name ?: it.exerciseName,
                trainingStepState = nextState,
                cooldownTargetTimestamp = null,
                cooldownSecondsRemaining = 0,
                isRoutineCompleted = allRoutineDone,
            )
        }
    }

    fun resetSessionMemory() {
        timerJob?.cancel()
        _trainingStepState.value = TrainingStepState.ReadyForNext(null)
        _uiState.value = WearActiveWorkoutUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        sensorPort?.stopHeartRateMonitoring()
    }
}
