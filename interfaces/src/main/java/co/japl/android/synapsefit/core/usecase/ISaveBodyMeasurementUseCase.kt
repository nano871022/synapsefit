package co.japl.android.synapsefit.core.usecase

interface ISaveBodyMeasurementUseCase {
    @Suppress("LongParameterList")
    suspend operator fun invoke(
        id: String?,
        weightKg: Double,
        chestCm: Double?,
        waistCm: Double?,
        hipCm: Double?,
        bicepLeftCm: Double?,
        bicepRightCm: Double?,
        thighLeftCm: Double?,
        thighRightCm: Double?,
        notes: String?,
    ): Result<Unit>
}
