@file:Suppress(
    "MagicNumber",
    "LongParameterList",
    "UnusedParameter",
    "UnusedPrivateProperty",
    "LongMethod",
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "MaxLineLength",
    "NestedBlockDepth",
)

package co.japl.android.synapsefit.app.controller.workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetExerciseMediaUseCase
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import co.japl.android.synapsefit.service.SynapseFitForegroundService
import co.japl.android.synapsefit.util.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class WorkoutSetUiModel(
    val setIndex: Int,
    val repsCompleted: String = "",
    val weightLiftedKg: String = "",
    val isCompleted: Boolean = false,
)

data class ExerciseSessionSummary(
    val exerciseName: String,
    val totalSetsCompleted: Int,
    val maxWeightLiftedKg: Double,
    val timeSpentSeconds: Long,
)

data class WorkoutSummary(
    val totalTimeSeconds: Long,
    val exerciseSummaries: List<ExerciseSessionSummary>,
)

sealed interface TrainingStepState {
    object Active : TrainingStepState

    data class Cooldown(val timeLeftSeconds: Int) : TrainingStepState

    object ReadyForNext : TrainingStepState
}

data class ActiveWorkoutUiState(
    val planId: String = "",
    val planTitle: String = "",
    val elapsedTimeSeconds: Long = 0L,
    val restTimerSecondsRemaining: Int? = null,
    val stepState: TrainingStepState = TrainingStepState.Active,
    val cooldownTargetTimestamp: Long? = null,
    val heartRateBpm: Int? = null,
    val currentExerciseName: String = "",
    val currentExerciseId: String = "",
    val exercises: List<ExerciseUiModel> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 1,
    val totalSetsForCurrentExercise: Int = 3,
    val targetRepsForCurrentSet: String = "10",
    val currentSetWeightKg: String = "",
    val currentSetReps: String = "",
    val isCurrentSetCompleted: Boolean = false,
    val isSessionComplete: Boolean = false,
    val summary: WorkoutSummary? = null,
    val exerciseVideoUrl: String? = null,
    val exerciseImageUrl: String? = null,
    val isImagePopupVisible: Boolean = false,
    val isMediaLoading: Boolean = false,
)

class ActiveWorkoutSessionViewModel(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort? = null,
    private val recordWorkoutSessionUseCase: RecordWorkoutSessionUseCase? = null,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
    private val getExerciseMediaUseCase: GetExerciseMediaUseCase? = null,
    private val context: Context? = null,
    private val wearStateMirrorPort: WearStateMirrorPort? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    private var sessionStartTimestamp: Long = 0L

    // Track time spent per exercise ID and weights per exercise ID
    private val exerciseStartTime = mutableMapOf<String, Long>()
    private val exerciseTimeSpent = mutableMapOf<String, Long>()
    private val exerciseCompletedSetsCount = mutableMapOf<String, Int>()
    private val exerciseMaxWeight = mutableMapOf<String, Double>()

    init {
        wearStateMirrorPort?.let { port ->
            viewModelScope.launch {
                port.liveSyncEvents.collect { event ->
                    handleIncomingLiveSyncEvent(event)
                }
            }
        }
    }

    private fun handleIncomingLiveSyncEvent(event: LiveSyncEvent) {
        when (event) {
            is LiveSyncEvent.StartSession -> {
                if (_uiState.value.planId != event.planId) {
                    startSession(event.planId)
                }
            }
            is LiveSyncEvent.SelectExercise -> {
                val state = _uiState.value
                val index =
                    state.exercises.indexOfFirst {
                        it.id == event.exerciseId || it.name == event.exerciseName
                    }
                if (index >= 0 && index != state.currentExerciseIndex) {
                    val nextEx = state.exercises[index]
                    _uiState.update {
                        it.copy(
                            currentExerciseIndex = index,
                            currentExerciseId = nextEx.id,
                            currentExerciseName = nextEx.name,
                            currentSetIndex = 1,
                            totalSetsForCurrentExercise = nextEx.targetSets,
                            targetRepsForCurrentSet = nextEx.targetReps,
                            isCurrentSetCompleted = false,
                            stepState = TrainingStepState.Active,
                            cooldownTargetTimestamp = null,
                        )
                    }
                }
            }
            is LiveSyncEvent.CompleteSet -> {
                val exId = event.exerciseId
                exerciseCompletedSetsCount[exId] = (exerciseCompletedSetsCount[exId] ?: 0) + 1
                if (event.weightKg > (exerciseMaxWeight[exId] ?: 0.0)) {
                    exerciseMaxWeight[exId] = event.weightKg
                }
                _uiState.update {
                    it.copy(
                        isCurrentSetCompleted = true,
                        currentSetReps = event.reps.toString(),
                    )
                }
                val cooldownSecs = event.cooldownDurationSeconds
                if (event.targetTimestamp != null && cooldownSecs != null) {
                    startRestTimer(cooldownSecs, event.targetTimestamp)
                }
            }
            is LiveSyncEvent.SkipExercise -> {
                nextSetOrExercise()
            }
            is LiveSyncEvent.FinishSession -> {
                if (!_uiState.value.isSessionComplete) {
                    finishSession()
                }
            }
        }
    }

    fun startSession(planId: String) {
        if (_uiState.value.planId == planId && _uiState.value.exercises.isNotEmpty()) {
            return
        }

        if (context != null && WorkoutSessionStateManager.hasActiveSession(context)) {
            val restored = WorkoutSessionStateManager.loadSession(context)
            if (restored != null && (planId.isBlank() || restored.uiState.planId == planId)) {
                sessionStartTimestamp = restored.sessionStartTimestamp
                exerciseTimeSpent.clear()
                exerciseTimeSpent.putAll(restored.exerciseTimeSpent)
                exerciseCompletedSetsCount.clear()
                exerciseCompletedSetsCount.putAll(restored.exerciseCompletedSets)
                exerciseMaxWeight.clear()
                exerciseMaxWeight.putAll(restored.exerciseMaxWeight)

                _uiState.update { restored.uiState }

                val currentEx = restored.uiState.exercises.getOrNull(restored.uiState.currentExerciseIndex)
                if (currentEx != null) {
                    exerciseStartTime[currentEx.id] = System.currentTimeMillis()
                    fetchExerciseMedia(currentEx)
                }

                startChronometer()
                restored.uiState.cooldownTargetTimestamp?.let { targetTs ->
                    val now = System.currentTimeMillis()
                    if (targetTs > now) {
                        val remainingSecs = ((targetTs - now) / 1000L).toInt()
                        startRestTimer(remainingSecs, targetTimestampOverride = targetTs)
                    } else {
                        _uiState.update {
                            it.copy(
                                restTimerSecondsRemaining = 0,
                                stepState = TrainingStepState.ReadyForNext,
                                cooldownTargetTimestamp = null,
                            )
                        }
                    }
                }
                return
            }
        }

        sessionStartTimestamp = System.currentTimeMillis()
        exerciseStartTime.clear()
        exerciseTimeSpent.clear()
        exerciseCompletedSetsCount.clear()
        exerciseMaxWeight.clear()

        startChronometer()

        viewModelScope.launch {
            val repository = workoutPlanRepositoryPort ?: return@launch
            val activePlanPair =
                if (planId.isNotBlank()) {
                    repository.getPlanWithExercises(planId).firstOrNull()
                } else {
                    val plan = repository.getActivePlan().firstOrNull()
                    if (plan != null) repository.getPlanWithExercises(plan.id).firstOrNull() else null
                }

            if (activePlanPair != null) {
                val (planObj, exercisesList) = activePlanPair
                context?.let { SynapseFitForegroundService.startWorkoutService(it, planObj.title) }

                val exercisesUi =
                    exercisesList.map { ex ->
                        ExerciseUiModel(
                            id = ex.id,
                            name = ex.name,
                            muscleGroup = ex.muscleGroup,
                            targetSets = ex.targetSets,
                            targetReps = ex.targetReps,
                            restSeconds = ex.restSeconds,
                            day = ex.day,
                            guideVideoUrl = ex.guideVideoUrl,
                            guideImageUrl = ex.guideImageUrl,
                        )
                    }

                val firstEx = exercisesUi.firstOrNull()
                firstEx?.let {
                    exerciseStartTime[it.id] = System.currentTimeMillis()
                    fetchExerciseMedia(it)
                }

                _uiState.update {
                    it.copy(
                        planId = planObj.id,
                        planTitle = planObj.title,
                        exercises = exercisesUi,
                        currentExerciseIndex = 0,
                        currentExerciseId = firstEx?.id ?: "",
                        currentExerciseName = firstEx?.name ?: "",
                        currentSetIndex = 1,
                        totalSetsForCurrentExercise = firstEx?.targetSets ?: 3,
                        targetRepsForCurrentSet = firstEx?.targetReps ?: "10",
                    )
                }

                wearStateMirrorPort?.sendEvent(
                    LiveSyncEvent.StartSession(
                        planId = planObj.id,
                        day = 1,
                        sessionStartTimestamp = sessionStartTimestamp,
                    ),
                )
                saveStateToPrefs()
            }
        }
    }

    private fun startChronometer() {
        timerJob?.cancel()
        timerJob =
            viewModelScope.launch {
                while (isActive) {
                    val elapsed = DateTimeUtils.calculateElapsedTimeSeconds(sessionStartTimestamp)
                    _uiState.update { it.copy(elapsedTimeSeconds = elapsed) }
                    WorkoutTimerManager.updateElapsedTime(elapsed)
                    delay(1000L.milliseconds)
                }
            }
    }

    fun onSetRepsChange(
        setIndex: Int,
        reps: String,
    ) {
        updateCurrentSetReps(reps)
    }

    fun onSetWeightChange(
        setIndex: Int,
        weightStr: String,
    ) {
        updateCurrentSetWeightKg(weightStr)
    }

    fun updateCurrentSetReps(reps: String) {
        _uiState.update { it.copy(currentSetReps = reps) }
        saveStateToPrefs()
    }

    fun updateCurrentSetWeightKg(weightStr: String) {
        _uiState.update { it.copy(currentSetWeightKg = weightStr) }
        saveStateToPrefs()
    }

    fun completeSet(setIndex: Int) {
        val state = _uiState.value
        val reps = state.currentSetReps.toIntOrNull() ?: extractMinimumReps(state.targetRepsForCurrentSet)
        val weight = state.currentSetWeightKg.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            val setDuration =
                if (sessionStartTimestamp > 0) DateTimeUtils.calculateElapsedTimeSeconds(sessionStartTimestamp) else 0L
            if (recordWorkoutSessionUseCase != null && state.currentExerciseId.isNotBlank() && reps > 0) {
                recordWorkoutSessionUseCase(
                    exerciseId = state.currentExerciseId,
                    repsCompleted = reps,
                    weightLiftedKg = weight,
                    heartRateBpm = state.heartRateBpm,
                    sourceDevice = SourceDevice.MOBILE,
                    durationSeconds = setDuration,
                )
            }

            val currentExId = state.currentExerciseId
            exerciseCompletedSetsCount[currentExId] = (exerciseCompletedSetsCount[currentExId] ?: 0) + 1
            val currentMax = exerciseMaxWeight[currentExId] ?: 0.0
            if (weight > currentMax) {
                exerciseMaxWeight[currentExId] = weight
            }

            val isLastSetForExercise = state.currentSetIndex >= state.totalSetsForCurrentExercise

            _uiState.update {
                it.copy(
                    isCurrentSetCompleted = true,
                    currentSetReps = reps.toString(),
                    currentSetWeightKg = weight.toString().removeSuffix(".0"),
                )
            }

            val restTime =
                if (isLastSetForExercise) {
                    WorkoutSessionStateManager.EXERCISE_CHANGE_REST_TIME_SECONDS
                } else {
                    state.exercises.getOrNull(state.currentExerciseIndex)?.restSeconds
                        ?: WorkoutSessionStateManager.DEFAULT_REST_TIME_SECONDS
                }

            val currentTimestamp = System.currentTimeMillis()
            val targetTimestamp = currentTimestamp + (restTime * 1000L)

            startRestTimer(restTime, targetTimestamp)

            wearStateMirrorPort?.sendEvent(
                LiveSyncEvent.CompleteSet(
                    exerciseId = currentExId,
                    setIndex = state.currentSetIndex,
                    reps = reps,
                    weightKg = weight,
                    targetTimestamp = targetTimestamp,
                    cooldownDurationSeconds = restTime,
                ),
            )

            saveStateToPrefs()
        }
    }

    private fun fetchExerciseMedia(exercise: ExerciseUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMediaLoading = true) }

            val queryFormatted = exercise.name.replace(" ", "+")
            val defaultVideo = "https://www.youtube.com/results?search_query=$queryFormatted"
            val defaultImage = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"

            val (rawVideo, rawImage) =
                if (getExerciseMediaUseCase != null) {
                    getExerciseMediaUseCase(
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        guideVideoUrl = exercise.guideVideoUrl,
                        guideImageUrl = exercise.guideImageUrl,
                    )
                } else {
                    Pair(exercise.guideVideoUrl, exercise.guideImageUrl)
                }

            val videoUrl = rawVideo?.takeIf { it.isNotBlank() } ?: defaultVideo
            val imageUrl = rawImage?.takeIf { it.isNotBlank() } ?: defaultImage

            _uiState.update {
                it.copy(
                    exerciseVideoUrl = videoUrl,
                    exerciseImageUrl = imageUrl,
                    isMediaLoading = false,
                )
            }
            saveStateToPrefs()
        }
    }

    fun showImagePopup() {
        _uiState.update { it.copy(isImagePopupVisible = true) }
    }

    fun hideImagePopup() {
        _uiState.update { it.copy(isImagePopupVisible = false) }
    }

    fun nextSetOrExercise() {
        restTimerJob?.cancel()
        val state = _uiState.value
        val isLastSetForExercise = state.currentSetIndex >= state.totalSetsForCurrentExercise

        if (!isLastSetForExercise) {
            nextSet(state)
        } else {
            val currentExId = state.currentExerciseId
            val timeSpent = DateTimeUtils.calculateElapsedTimeSeconds(exerciseStartTime[currentExId])
            exerciseTimeSpent[currentExId] = (exerciseTimeSpent[currentExId] ?: 0L) + timeSpent

            val isLastExercise = state.currentExerciseIndex + 1 >= state.exercises.size
            if (isLastExercise) {
                finishSession()
            } else {
                nextExercise(state, timeSpent)
            }
        }
        saveStateToPrefs()
    }

    private fun nextExercise(
        state: ActiveWorkoutUiState,
        timeSpent: Long,
    ) {
        val nextExIndex = state.currentExerciseIndex + 1
        val nextEx = state.exercises[nextExIndex]
        exerciseStartTime[nextEx.id] = System.currentTimeMillis()
        fetchExerciseMedia(nextEx)

        _uiState.update {
            it.copy(
                currentExerciseIndex = nextExIndex,
                currentExerciseId = nextEx.id,
                currentExerciseName = nextEx.name,
                currentSetIndex = 1,
                totalSetsForCurrentExercise = nextEx.targetSets,
                targetRepsForCurrentSet = nextEx.targetReps,
                currentSetReps = "",
                currentSetWeightKg = "",
                isCurrentSetCompleted = false,
                restTimerSecondsRemaining = null,
                stepState = TrainingStepState.Active,
                cooldownTargetTimestamp = null,
            )
        }
        WorkoutTimerManager.updateRestTime(null)

        viewModelScope.launch {
            wearStateMirrorPort?.sendEvent(
                LiveSyncEvent.SelectExercise(
                    exerciseId = nextEx.id,
                    exerciseName = nextEx.name,
                ),
            )
        }
    }

    private fun nextSet(state: ActiveWorkoutUiState) {
        val nextSetIdx = state.currentSetIndex + 1
        _uiState.update {
            it.copy(
                currentSetIndex = nextSetIdx,
                isCurrentSetCompleted = false,
                currentSetReps = state.currentSetReps,
                restTimerSecondsRemaining = null,
                stepState = TrainingStepState.Active,
                cooldownTargetTimestamp = null,
            )
        }
        WorkoutTimerManager.updateRestTime(null)
    }

    fun startRestTimer(
        cooldownDurationSeconds: Int,
        targetTimestampOverride: Long? = null,
    ) {
        restTimerJob?.cancel()
        val currentTimestamp = System.currentTimeMillis()
        val targetTimestamp = targetTimestampOverride ?: (currentTimestamp + (cooldownDurationSeconds * 1000L))

        restTimerJob =
            viewModelScope.launch {
                while (isActive) {
                    val now = System.currentTimeMillis()
                    val remainingMillis = targetTimestamp - now

                    if (remainingMillis <= 0) {
                        _uiState.update {
                            it.copy(
                                restTimerSecondsRemaining = 0,
                                stepState = TrainingStepState.ReadyForNext,
                                cooldownTargetTimestamp = null,
                            )
                        }
                        WorkoutTimerManager.updateRestTime(0)
                        break
                    } else {
                        val remainingSeconds = (remainingMillis / 1000L).toInt() + (if (remainingMillis % 1000L > 0) 1 else 0)
                        _uiState.update {
                            it.copy(
                                restTimerSecondsRemaining = remainingSeconds,
                                stepState = TrainingStepState.Cooldown(remainingSeconds),
                                cooldownTargetTimestamp = targetTimestamp,
                            )
                        }
                        WorkoutTimerManager.updateRestTime(remainingSeconds)
                    }
                    delay(500L)
                }
            }
    }

    fun updateHeartRateBpm(bpm: Int) {
        _uiState.update { it.copy(heartRateBpm = bpm) }
    }

    fun finishSession() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        WorkoutTimerManager.reset()

        val state = _uiState.value
        val currentExId = state.currentExerciseId
        if (currentExId.isNotBlank() && exerciseStartTime.containsKey(currentExId)) {
            val timeSpent = DateTimeUtils.calculateElapsedTimeSeconds(exerciseStartTime[currentExId])
            exerciseTimeSpent[currentExId] = (exerciseTimeSpent[currentExId] ?: 0L) + timeSpent
        }

        val summaries =
            state.exercises.map { ex ->
                ExerciseSessionSummary(
                    exerciseName = ex.name,
                    totalSetsCompleted = exerciseCompletedSetsCount[ex.id] ?: 0,
                    maxWeightLiftedKg = exerciseMaxWeight[ex.id] ?: 0.0,
                    timeSpentSeconds = exerciseTimeSpent[ex.id] ?: 0L,
                )
            }

        val workoutSummary =
            WorkoutSummary(
                totalTimeSeconds = state.elapsedTimeSeconds,
                exerciseSummaries = summaries,
            )

        _uiState.update {
            it.copy(
                isSessionComplete = true,
                summary = workoutSummary,
            )
        }

        viewModelScope.launch {
            wearStateMirrorPort?.sendEvent(
                LiveSyncEvent.FinishSession(
                    planId = state.planId,
                    totalDurationSeconds = state.elapsedTimeSeconds,
                ),
            )
        }

        context?.let {
            WorkoutSessionStateManager.clearSession(it)
            SynapseFitForegroundService.stopService(it)
        }
    }

    private fun saveStateToPrefs() {
        context?.let {
            WorkoutSessionStateManager.saveSession(
                context = it,
                uiState = _uiState.value,
                sessionStartTimestamp = sessionStartTimestamp,
                exerciseTimeSpent = exerciseTimeSpent,
                exerciseCompletedSets = exerciseCompletedSetsCount,
                exerciseMaxWeight = exerciseMaxWeight,
            )
        }
    }

    private fun extractMinimumReps(target: String): Int {
        val firstPart = target.substringBefore('-').substringBefore('–').trim()
        return firstPart.filter { it.isDigit() }.toIntOrNull() ?: 1
    }
}
