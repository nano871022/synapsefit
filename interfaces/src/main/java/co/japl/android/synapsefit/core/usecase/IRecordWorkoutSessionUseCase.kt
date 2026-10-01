package co.japl.android.synapsefit.core.usecase

interface IRecordWorkoutSessionUseCase {
    suspend operator fun invoke(
        exerciseId: String,
        repsCompleted: Int,
        weightLiftedKg: Double,
        heartRateBpm: Int?,
    ): Result<Unit>
}
