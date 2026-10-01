package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IActiveWorkoutUseCase {
    val activeSessionState: StateFlow<ActiveWorkoutSessionState?>

    fun getPlanWithExercisesForDay(
        planId: String,
        day: Int,
    ): Flow<Pair<WorkoutPlan, List<WorkoutSessionItem>>?>

    fun getActiveSessionState(): Flow<ActiveWorkoutSessionState>

    fun selectExercise(exerciseId: String)

    suspend fun completeSet(
        reps: Int,
        weightKg: Double,
        heartRateBpm: Int?,
        restSeconds: Int,
        workoutDurationSeconds: Long,
    ): Result<TrainingStepState>

    @Suppress("LongParameterList")
    suspend fun recordWorkoutLog(
        planId: String,
        day: Int,
        exerciseId: String,
        reps: Int,
        weightKg: Double,
        durationSeconds: Long,
        heartRateBpm: Int?,
    )

    fun advanceToNextExercise()

    fun finishSession()

    suspend fun resolveExerciseMedia(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String,
        guideImageUrl: String,
    ): Pair<String?, String?>

    suspend fun sendLiveSyncEvent(event: LiveSyncEvent)
}
