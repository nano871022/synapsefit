@file:Suppress(
    "MagicNumber",
    "LongParameterList",
    "UnusedParameter",
    "LongMethod",
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "MaxLineLength",
)

package co.japl.android.synapsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.RecordWorkoutSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort? = null,
    private val llmClientPort: LlmClientPort? = null,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort? = null,
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

    fun startSession(planId: String) {
        if (_uiState.value.planId == planId && _uiState.value.exercises.isNotEmpty()) {
            return
        }
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

                val planDays =
                    mappedExercises.mapNotNull { ex ->
                        Regex("\\[Día (\\d+)\\]", RegexOption.IGNORE_CASE).find(ex.name)?.groupValues?.get(1)?.toIntOrNull()
                    }.distinct().sorted()

                val totalPlanDays = if (planDays.isNotEmpty()) planDays.maxOrNull() ?: 1 else 1

                val latestLogs =
                    workoutLogRepositoryPort?.getLatestLogsForPlan(planId)?.let { flow ->
                        flow.firstOrNull()
                    } ?: emptyList()

                val lastLog = latestLogs.firstOrNull()
                val lastEx = mappedExercises.find { it.id == lastLog?.exerciseId }
                val lastDay =
                    lastEx?.let { ex ->
                        Regex("\\[Día (\\d+)\\]", RegexOption.IGNORE_CASE).find(ex.name)?.groupValues?.get(1)?.toIntOrNull()
                    } ?: 0

                val activeDayNumber = if (lastDay == 0) 1 else (lastDay % totalPlanDays) + 1

                val filteredForActiveDay =
                    mappedExercises.filter { ex ->
                        val match = Regex("\\[Día (\\d+)\\]", RegexOption.IGNORE_CASE).find(ex.name)
                        if (match != null) {
                            match.groupValues[1].toIntOrNull() == activeDayNumber
                        } else {
                            true
                        }
                    }.ifEmpty { mappedExercises }

                val firstExercise = filteredForActiveDay.firstOrNull()
                if (firstExercise != null) {
                    exerciseStartTime[firstExercise.id] = System.currentTimeMillis()
                    fetchExerciseMedia(firstExercise)
                }

                sessionStartTimestamp = System.currentTimeMillis()

                _uiState.update {
                    it.copy(
                        planTitle = "${plan.title} (Día $activeDayNumber)",
                        exercises = filteredForActiveDay,
                        currentExerciseIndex = 0,
                        currentExerciseId = firstExercise?.id ?: "",
                        currentExerciseName = firstExercise?.name ?: "",
                        currentSetIndex = 1,
                        totalSetsForCurrentExercise = firstExercise?.targetSets ?: 3,
                        targetRepsForCurrentSet = firstExercise?.targetReps ?: "10",
                        currentSetReps = firstExercise?.targetReps ?: "10",
                        currentSetWeightKg = "",
                        isCurrentSetCompleted = false,
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
                while (isActive) {
                    delay(1000L)
                    val elapsed = (System.currentTimeMillis() - sessionStartTimestamp) / 1000L
                    _uiState.update { it.copy(elapsedTimeSeconds = elapsed) }
                }
            }
    }

    fun onSetRepsChange(
        setIndex: Int,
        reps: String,
    ) {
        _uiState.update { it.copy(currentSetReps = reps) }
    }

    fun onSetWeightChange(
        setIndex: Int,
        weight: String,
    ) {
        _uiState.update { it.copy(currentSetWeightKg = weight) }
    }

    fun completeSet(setIndex: Int) {
        val state = _uiState.value
        val reps = state.currentSetReps.toIntOrNull() ?: 0
        val weight = state.currentSetWeightKg.toDoubleOrNull() ?: 0.0

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

            val currentExId = state.currentExerciseId
            exerciseCompletedSetsCount[currentExId] = (exerciseCompletedSetsCount[currentExId] ?: 0) + 1
            val currentMax = exerciseMaxWeight[currentExId] ?: 0.0
            if (weight > currentMax) {
                exerciseMaxWeight[currentExId] = weight
            }

            val isLastSetForExercise = state.currentSetIndex >= state.totalSetsForCurrentExercise
            if (isLastSetForExercise) {
                // Record time spent for finished exercise
                val startT = exerciseStartTime[currentExId] ?: System.currentTimeMillis()
                val timeSpent = (System.currentTimeMillis() - startT) / 1000
                exerciseTimeSpent[currentExId] = (exerciseTimeSpent[currentExId] ?: 0L) + timeSpent

                val isLastExercise = state.currentExerciseIndex + 1 >= state.exercises.size
                if (isLastExercise) {
                    finishSession()
                } else {
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
                            currentSetReps = nextEx.targetReps,
                            currentSetWeightKg = "",
                            isCurrentSetCompleted = false,
                            restTimerSecondsRemaining = null,
                        )
                    }
                }
            } else {
                val nextSetIdx = state.currentSetIndex + 1
                _uiState.update {
                    it.copy(
                        currentSetIndex = nextSetIdx,
                        isCurrentSetCompleted = false,
                        restTimerSecondsRemaining = null,
                    )
                }
                val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)
                startRestTimer(currentExercise?.restSeconds ?: 60)
            }
        }
    }

    private fun fetchExerciseMedia(exercise: ExerciseUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMediaLoading = true) }

            if (!exercise.guideVideoUrl.isNullOrBlank() || !exercise.guideImageUrl.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        exerciseVideoUrl = exercise.guideVideoUrl,
                        exerciseImageUrl = exercise.guideImageUrl,
                        isMediaLoading = false,
                    )
                }
                return@launch
            }

            val config = llmConfigRepositoryPort?.getActiveConfig()?.firstOrNull()
            var videoUrl: String? = null
            var imageUrl: String? = null

            if (config != null && llmClientPort != null) {
                val result = llmClientPort.fetchExerciseMedia(exercise.name, config)
                result.onSuccess { (v, img) ->
                    videoUrl = v
                    imageUrl = img
                }.onFailure {
                    val queryFormatted = exercise.name.replace(" ", "+")
                    videoUrl = "https://www.youtube.com/results?search_query=$queryFormatted"
                    imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
                }
            } else {
                val queryFormatted = exercise.name.replace(" ", "+")
                videoUrl = "https://www.youtube.com/results?search_query=$queryFormatted"
                imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
            }

            _uiState.update {
                it.copy(
                    exerciseVideoUrl = videoUrl,
                    exerciseImageUrl = imageUrl,
                    isMediaLoading = false,
                )
            }

            if (exercise.id.isNotBlank()) {
                workoutPlanRepositoryPort?.updateExerciseMedia(exercise.id, videoUrl, imageUrl)
            }
        }
    }

    fun showImagePopup() {
        _uiState.update { it.copy(isImagePopupVisible = true) }
    }

    fun hideImagePopup() {
        _uiState.update { it.copy(isImagePopupVisible = false) }
    }

    fun nextSetOrExercise() {
        val state = _uiState.value
        val currentExId = state.currentExerciseId

        if (state.currentSetIndex < state.totalSetsForCurrentExercise) {
            // Next set in the same exercise
            val nextSetIdx = state.currentSetIndex + 1
            _uiState.update {
                it.copy(
                    currentSetIndex = nextSetIdx,
                    isCurrentSetCompleted = false,
                    restTimerSecondsRemaining = null,
                )
            }
        } else {
            // Current exercise complete, record time spent
            val startT = exerciseStartTime[currentExId] ?: System.currentTimeMillis()
            val timeSpent = (System.currentTimeMillis() - startT) / 1000
            exerciseTimeSpent[currentExId] = (exerciseTimeSpent[currentExId] ?: 0L) + timeSpent

            if (state.currentExerciseIndex + 1 < state.exercises.size) {
                val nextExIndex = state.currentExerciseIndex + 1
                val nextEx = state.exercises[nextExIndex]
                exerciseStartTime[nextEx.id] = System.currentTimeMillis()

                _uiState.update {
                    it.copy(
                        currentExerciseIndex = nextExIndex,
                        currentExerciseId = nextEx.id,
                        currentExerciseName = nextEx.name,
                        currentSetIndex = 1,
                        totalSetsForCurrentExercise = nextEx.targetSets,
                        targetRepsForCurrentSet = nextEx.targetReps,
                        currentSetReps = nextEx.targetReps,
                        currentSetWeightKg = "",
                        isCurrentSetCompleted = false,
                        restTimerSecondsRemaining = null,
                    )
                }
            } else {
                finishSession()
            }
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

        val state = _uiState.value
        val currentExId = state.currentExerciseId
        if (currentExId.isNotBlank() && exerciseStartTime.containsKey(currentExId)) {
            val startT = exerciseStartTime[currentExId] ?: System.currentTimeMillis()
            val timeSpent = (System.currentTimeMillis() - startT) / 1000
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
    }
}
