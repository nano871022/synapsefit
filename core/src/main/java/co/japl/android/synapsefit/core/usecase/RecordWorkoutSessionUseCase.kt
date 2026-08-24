package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort

class RecordWorkoutSessionUseCase(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) {
    @Suppress("LongParameterList", "TooGenericExceptionCaught", "ReturnCount")
    suspend operator fun invoke(
        exerciseId: String,
        repsCompleted: Int,
        weightLiftedKg: Double,
        heartRateBpm: Int? = null,
        sourceDevice: SourceDevice = SourceDevice.MOBILE,
        timestamp: Long = System.currentTimeMillis(),
    ): Result<WorkoutLog> {
        if (exerciseId.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Exercise ID cannot be empty"))
        }
        if (repsCompleted <= 0) {
            return Result.failure(IllegalArgumentException("Reps completed must be greater than 0"))
        }
        if (weightLiftedKg < 0) {
            return Result.failure(IllegalArgumentException("Weight lifted cannot be negative"))
        }
        if (heartRateBpm != null && heartRateBpm <= 0) {
            return Result.failure(IllegalArgumentException("Heart rate BPM must be greater than 0"))
        }

        val now = System.currentTimeMillis()
        val log =
            WorkoutLog(
                id = java.util.UUID.randomUUID().toString(),
                exerciseId = exerciseId,
                repsCompleted = repsCompleted,
                weightLiftedKg = weightLiftedKg,
                heartRateBpm = heartRateBpm,
                sourceDevice = sourceDevice,
                timestamp = timestamp,
                createdAt = now,
                updatedAt = now,
            )

        return try {
            workoutLogRepositoryPort.saveLog(log)
            Result.success(log)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
