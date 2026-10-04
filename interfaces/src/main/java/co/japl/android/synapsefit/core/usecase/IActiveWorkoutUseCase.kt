package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IActiveWorkoutUseCase {
    fun getPlanWithExercisesForDay(
        planId: String,
        day: Int,
    ): Flow<Pair<WorkoutPlan, List<Exercise>>?>

    fun getActiveSessionState(): Flow<ActiveWorkoutSessionState>

    @Suppress("LongParameterList")
    suspend fun recordWorkoutLog(
        planId: String,
        day: Int,
        exerciseId: String,
        reps: Int,
        weightKg: Double,
        durationSeconds: Long,
        heartRateBpm: Int? = null,
    ): Result<Unit>

    suspend fun resolveExerciseMedia(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String,
        guideImageUrl: String,
    ): Pair<String?, String?>

    suspend fun sendLiveSyncEvent(event: LiveSyncEvent)
}
