package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog

interface IRecordWorkoutSessionUseCase {
    @Suppress("LongParameterList")
    suspend operator fun invoke(
        exerciseId: String,
        repsCompleted: Int,
        weightLiftedKg: Double,
        heartRateBpm: Int? = null,
        sourceDevice: SourceDevice = SourceDevice.MOBILE,
        durationSeconds: Long = 0L,
        timestamp: Long = System.currentTimeMillis(),
    ): Result<WorkoutLog>
}
