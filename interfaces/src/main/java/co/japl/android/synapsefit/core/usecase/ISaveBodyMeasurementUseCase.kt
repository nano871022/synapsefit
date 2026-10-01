package co.japl.android.synapsefit.core.usecase

interface ISaveBodyMeasurementUseCase {
    @Suppress("LongParameterList")
    suspend operator fun invoke(
        id: String? = null,
        weightKg: Double,
        chestCm: Double? = null,
        waistCm: Double? = null,
        hipCm: Double? = null,
        bicepLeftCm: Double? = null,
        bicepRightCm: Double? = null,
        thighLeftCm: Double? = null,
        thighRightCm: Double? = null,
        notes: String? = null,
    ): Result<Unit>
}
