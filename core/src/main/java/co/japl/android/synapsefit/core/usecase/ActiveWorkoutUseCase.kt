@file:Suppress("LongParameterList", "TooGenericExceptionCaught", "UnusedParameter")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

class ActiveWorkoutUseCase( : IActiveWorkoutUseCase
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort? = null,
    private val wearStateMirrorPort: WearStateMirrorPort? = null,
    private val getExerciseMediaUseCase: GetExerciseMediaUseCase? = null,
), IActiveWorkoutUseCase : IActiveWorkoutUseCase {
    override fun getPlanWithExercisesForDay(
        planId: String,
        day: Int,
    ): Flow<Pair<WorkoutPlan, List<Exercise>>?> {
        return workoutPlanRepositoryPort.getPlanWithExercisesForDay(planId, day)
    }

    override fun getActiveSessionState(): Flow<ActiveWorkoutSessionState> {
        return activeSessionRepositoryPort?.getActiveSessionState()
            ?: kotlinx.coroutines.flow.flowOf(ActiveWorkoutSessionState())
    }

    override suspend override fun recordWorkoutLog(
        planId: String,
        day: Int,
        exerciseId: String,
        reps: Int,
        weightKg: Double,
        durationSeconds: Long,
        heartRateBpm: Int? = null,
    ): Result<Unit> {
        override val now = System.currentTimeMillis()
        override val log =
            WorkoutLog(
                id = java.util.UUID.randomUUID().toString(),
                exerciseId = exerciseId,
                repsCompleted = reps,
                weightLiftedKg = weightKg,
                heartRateBpm = heartRateBpm,
                sourceDevice = SourceDevice.MOBILE,
                durationSeconds = durationSeconds,
                timestamp = now,
                createdAt = now,
                updatedAt = now,
            )
        return try {
            workoutLogRepositoryPort.saveLog(log)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend override fun resolveExerciseMedia(
        exerciseId: String,
        exerciseName: String,
        guideVideoUrl: String,
        guideImageUrl: String,
    ): Pair<String?, String?> {
        return getExerciseMediaUseCase?.invoke(exerciseId, exerciseName, guideVideoUrl, guideImageUrl)
            ?: Pair(guideVideoUrl, guideImageUrl)
    }

    override suspend override fun sendLiveSyncEvent(event: LiveSyncEvent) {
        wearStateMirrorPort?.sendEvent(event)
    }
}
