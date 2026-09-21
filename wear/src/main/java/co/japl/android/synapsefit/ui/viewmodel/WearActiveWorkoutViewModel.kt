package co.japl.android.synapsefit.ui.viewmodel

import android.view.KeyEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
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
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort? = null,
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
                state.exerciseSessions.filter { it.isCompleted }.mapNotNull { it.exerciseId }
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

        val now = DateTimeUtils.getCurrentTimestamp()
        val startTs = state.sessionStartTimestamp ?: now
        val elapsed = DateTimeUtils.calculateElapsedTimeSeconds(startTs, now)
        _trainingStepState.value = nextState
        _uiState.update {
            val exChanged = it.activeExerciseId != event.currentExerciseId
            it.copy(
                isLiveSyncActive = true,
                isSessionStarted = true,
                exerciseSessions = updatedSessions,
                activeExerciseIndex = targetIndex,
                exerciseName = activeSession?.name ?: it.exerciseName,
                cooldownTargetTimestamp = event.cooldownTargetTimestamp,
                trainingStepState = nextState,
                activePlanDayId = event.day,
                sessionStartTimestamp = startTs,
                workoutDurationSeconds = elapsed,
                activeExerciseId = event.currentExerciseId,
                exerciseStartTimestamp = if (exChanged) now else it.exerciseStartTimestamp,
            )
        }
        persistActiveSessionState()
        startWorkoutTimer()
    }

    fun loadPlanData(
        planId: String,
        day: Int,
    ) {
        val repository = workoutPlanRepositoryPort ?: return
        viewModelScope.launch {
            val targetPlan =
                repository.getActivePlan().firstOrNull()
                    ?: repository.getPlanWithExercises(planId).firstOrNull()?.first
            val pair = repository.getPlanWithExercises(planId).firstOrNull()
            val allExercises = pair?.second ?: emptyList()
            val exercises = allExercises.filter { it.day == day }.ifEmpty { allExercises }

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

            _uiState.update { current ->
                current.copy(
                    activePlanId = planId,
                    activePlanTitle = targetPlan?.title ?: current.activePlanTitle,
                    currentDay = day,
                    availableExercises = exercises,
                    exerciseSessions = sessions,
                )
            }
        }
    }

    fun loadActivePlanData() {
        val repository = workoutPlanRepositoryPort ?: return
        viewModelScope.launch {
            repository.getActivePlan().collect { activePlan ->
                if (activePlan == null) return@collect
                if (_uiState.value.exerciseSessions.isNotEmpty()) return@collect
                val pair = repository.getPlanWithExercises(activePlan.id).firstOrNull()
                val allExercises = pair?.second ?: emptyList()
                val exercises = allExercises.filter { it.day == 1 }.ifEmpty { allExercises }

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

                _uiState.update { current ->
                    current.copy(
                        activePlanId = activePlan.id,
                        activePlanTitle = activePlan.title,
                        currentDay = 1,
                        availableExercises = exercises,
                        exerciseSessions = sessions,
                    )
                }
            }
        }
    }

    fun selectExercise(
        exercise: Exercise,
        index: Int,
    ) {
        sensorPort?.startHeartRateMonitoring()
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
        val currentSet = (targetSession.completedSets + 1).coerceAtMost(targetSession.targetSets)
        val activeState = TrainingStepState.Active(targetSession, currentSet)
        val now = DateTimeUtils.getCurrentTimestamp()
        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                activeExerciseIndex = index,
                exerciseName = exercise.name,
                trainingStepState = activeState,
                activeExerciseId = exercise.id,
                exerciseStartTimestamp = now,
            )
        }
        persistActiveSessionState()
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
            KeyEvent.KEYCODE_STEM_1, KeyEvent.KEYCODE_DPAD_UP -> {
                incrementFocusedInput()
                true
            }
            KeyEvent.KEYCODE_STEM_2, KeyEvent.KEYCODE_DPAD_DOWN -> {
                decrementFocusedInput()
                true
            }
            else -> false
        }
    }

    private fun incrementFocusedInput() {
        if (_uiState.value.focusedInput == FocusedInput.REPS) {
            incrementReps()
        } else {
            incrementWgt()
        }
    }

    private fun decrementFocusedInput() {
        if (_uiState.value.focusedInput == FocusedInput.REPS) {
            decrementReps()
        } else {
            decrementWgt()
        }
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
        _uiState.update { current ->
            if (current.focusedInput == FocusedInput.REPS) {
                current.copy(currentReps = value, isNumericKeypadOpen = false)
            } else {
                current.copy(currentWeight = value.toShort(), isNumericKeypadOpen = false)
            }
        }
    }

    fun loadExerciseSessions(sessions: List<ExerciseSession>) {
        val firstSession = sessions.firstOrNull()
        val activeState =
            firstSession?.let {
                val currentSet = (it.completedSets + 1).coerceAtMost(it.targetSets)
                TrainingStepState.Active(it, currentSet)
            } ?: TrainingStepState.ReadyForNext(null)

        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                exerciseSessions = sessions,
                trainingStepState = activeState,
                exerciseName = firstSession?.name ?: it.exerciseName,
                activeExerciseId = firstSession?.exerciseId ?: "",
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
        recalculateCooldownTimer(forceFinish = true)
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
                        val startTs = _uiState.value.sessionStartTimestamp
                        val now = DateTimeUtils.getCurrentTimestamp()
                        val elapsed =
                            if (startTs > 0L) {
                                DateTimeUtils.calculateElapsedTimeSeconds(startTs, now)
                            } else {
                                _uiState.value.workoutDurationSeconds + 1L
                            }
                        _uiState.update { it.copy(workoutDurationSeconds = elapsed) }

                        if (_trainingStepState.value is TrainingStepState.Cooldown) {
                            recalculateCooldownTimer()
                        }
                    }
                }
            }
    }

    fun togglePauseResume() {
        val nextPaused = !_uiState.value.isPaused
        if (nextPaused) {
            _trainingStepState.value = TrainingStepState.Paused(_trainingStepState.value)
        } else {
            val paused = _trainingStepState.value as? TrainingStepState.Paused
            if (paused != null) {
                _trainingStepState.value = paused.previousState
            }
        }
        _uiState.update { it.copy(isPaused = nextPaused) }
    }

    fun startSession() {
        sensorPort?.startHeartRateMonitoring()
        val now = DateTimeUtils.getCurrentTimestamp()
        val startTs = if (_uiState.value.sessionStartTimestamp > 0L) _uiState.value.sessionStartTimestamp else now
        val initialElapsed = DateTimeUtils.calculateElapsedTimeSeconds(startTs, now)
        _uiState.update { current ->
            val firstExId =
                current.exerciseSessions.getOrNull(current.activeExerciseIndex)?.exerciseId
                    ?: current.availableExercises.firstOrNull()?.id ?: ""
            val exStartTs =
                if (current.exerciseStartTimestamp > 0L) current.exerciseStartTimestamp else now
            current.copy(
                isSessionStarted = true,
                activePlanDayId = current.currentDay,
                sessionStartTimestamp = startTs,
                workoutDurationSeconds = initialElapsed,
                activeExerciseId = current.activeExerciseId.ifBlank { firstExId },
                exerciseStartTimestamp = exStartTs,
            )
        }
        persistActiveSessionState()
        startWorkoutTimer()
    }

    fun finishSession() {
        _uiState.update { it.copy(isSessionStarted = false, isRoutineCompleted = true) }
        syncPort?.flushSyncQueue()
    }

    fun completeSet() {
        val currentState = _trainingStepState.value
        if (currentState !is TrainingStepState.Active) return

        val session = currentState.exerciseSession
        val completed = session.completedSets + 1
        val isDone = completed >= session.targetSets
        val updatedSession = session.copy(completedSets = completed, isCompleted = isDone)

        val updatedList =
            _uiState.value.exerciseSessions.map {
                if (it.exerciseId == session.exerciseId) updatedSession else it
            }

        val allDone = updatedList.all { it.isCompleted }

        if (allDone) {
            _trainingStepState.value = TrainingStepState.ReadyForNext(null)
            _uiState.update {
                it.copy(
                    exerciseSessions = updatedList,
                    isRoutineCompleted = true,
                    trainingStepState = TrainingStepState.ReadyForNext(null),
                )
            }
            persistActiveSessionState()
            syncPort?.flushSyncQueue()
        } else {
            val now = DateTimeUtils.getCurrentTimestamp()
            val restSecs = (session.restSeconds.takeIf { it > 0 } ?: DEFAULT_REST_SECONDS.toInt()).toLong()
            val targetTimestamp = now + (restSecs * MILLIS_PER_SECOND)
            val remainingMillis = restSecs * MILLIS_PER_SECOND

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
            persistActiveSessionState()
        }
    }

    fun recalculateCooldownTimer(forceFinish: Boolean = false) {
        val currentState = _trainingStepState.value
        if (currentState !is TrainingStepState.Cooldown && !forceFinish) return

        val cdTarget =
            _uiState.value.cooldownTargetTimestamp
                ?: (currentState as? TrainingStepState.Cooldown)?.targetTimestamp
                ?: 0L

        val remainingMillis =
            if (forceFinish) 0L else TrainingStepState.calculateRemainingMillis(cdTarget)

        if (remainingMillis > 0L && currentState is TrainingStepState.Cooldown) {
            val updatedState =
                TrainingStepState.Cooldown(
                    exerciseSession = currentState.exerciseSession,
                    targetTimestamp = cdTarget,
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

            val allRoutineDone =
                _uiState.value.exerciseSessions.isNotEmpty() &&
                    _uiState.value.exerciseSessions.all { it.isCompleted }

            if (nextIncomplete != null && !allRoutineDone) {
                val currentSet = (nextIncomplete.completedSets + 1).coerceAtMost(nextIncomplete.targetSets)
                val activeState = TrainingStepState.Active(nextIncomplete, currentSet)
                val now = DateTimeUtils.getCurrentTimestamp()
                _trainingStepState.value = activeState
                _uiState.update {
                    it.copy(
                        activeExerciseIndex = nextIndex,
                        exerciseName = nextIncomplete.name,
                        trainingStepState = activeState,
                        activeExerciseId = nextIncomplete.exerciseId,
                        exerciseStartTimestamp = now,
                        cooldownTargetTimestamp = null,
                        cooldownSecondsRemaining = 0,
                        isRoutineCompleted = false,
                    )
                }
            } else {
                val nextState = TrainingStepState.ReadyForNext(nextIncomplete)
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
            persistActiveSessionState()
        }
    }

    fun startNextExercise() {
        val currentState = _trainingStepState.value
        val nextSession =
            (currentState as? TrainingStepState.ReadyForNext)?.nextExerciseSession
                ?: _uiState.value.exerciseSessions.firstOrNull { !it.isCompleted }
                ?: return

        val currentSet = (nextSession.completedSets + 1).coerceAtMost(nextSession.targetSets)
        val activeState = TrainingStepState.Active(nextSession, currentSet)

        val now = DateTimeUtils.getCurrentTimestamp()
        _trainingStepState.value = activeState
        val nextIndex =
            _uiState.value.exerciseSessions.indexOfFirst {
                it.exerciseId == nextSession.exerciseId
            }.coerceAtLeast(0)

        _uiState.update {
            it.copy(
                activeExerciseIndex = nextIndex,
                exerciseName = nextSession.name,
                trainingStepState = activeState,
                activeExerciseId = nextSession.exerciseId,
                exerciseStartTimestamp = now,
            )
        }
        persistActiveSessionState()
    }

    fun navigateToNextExercise() {
        val sessions = _uiState.value.exerciseSessions
        if (sessions.isEmpty()) return
        val nextIndex = (_uiState.value.activeExerciseIndex + 1) % sessions.size
        val nextSession = sessions[nextIndex]
        val currentSet = (nextSession.completedSets + 1).coerceAtMost(nextSession.targetSets)
        val activeState = TrainingStepState.Active(nextSession, currentSet)
        val now = DateTimeUtils.getCurrentTimestamp()
        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                activeExerciseIndex = nextIndex,
                exerciseName = nextSession.name,
                trainingStepState = activeState,
                activeExerciseId = nextSession.exerciseId,
                exerciseStartTimestamp = now,
            )
        }
        persistActiveSessionState()
    }

    fun navigateToPreviousExercise() {
        val sessions = _uiState.value.exerciseSessions
        if (sessions.isEmpty()) return
        val prevIndex =
            if (_uiState.value.activeExerciseIndex - 1 < 0) {
                sessions.size - 1
            } else {
                _uiState.value.activeExerciseIndex - 1
            }
        val prevSession = sessions[prevIndex]
        val currentSet = (prevSession.completedSets + 1).coerceAtMost(prevSession.targetSets)
        val activeState = TrainingStepState.Active(prevSession, currentSet)
        val now = DateTimeUtils.getCurrentTimestamp()
        _trainingStepState.value = activeState
        _uiState.update {
            it.copy(
                activeExerciseIndex = prevIndex,
                exerciseName = prevSession.name,
                trainingStepState = activeState,
                activeExerciseId = prevSession.exerciseId,
                exerciseStartTimestamp = now,
            )
        }
        persistActiveSessionState()
    }

    private fun persistActiveSessionState() {
        val repo = activeSessionRepositoryPort ?: return
        val state = _uiState.value
        val isStarted = state.isSessionStarted && !state.isRoutineCompleted
        repo.updateActiveSessionState(
            ActiveWorkoutSessionState(
                isActive = isStarted,
                activePlanDayId = "${state.activePlanId ?: ""}_${state.currentDay}",
                planId = state.activePlanId ?: "",
                day = state.currentDay,
                sessionStartTimestamp = if (isStarted) state.sessionStartTimestamp else 0L,
                activeExerciseId = if (isStarted) state.activeExerciseId else "",
                exerciseStartTimestamp = if (isStarted) state.exerciseStartTimestamp else 0L,
            ),
        )
    }

    fun resetSessionMemory() {
        timerJob?.cancel()
        activeSessionRepositoryPort?.clearActiveSession()
        _trainingStepState.value = TrainingStepState.ReadyForNext(null)
        _uiState.value = WearActiveWorkoutUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        sensorPort?.stopHeartRateMonitoring()
    }
}
