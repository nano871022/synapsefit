package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort

class SaveBodyMeasurementUseCase(
    private val repositoryPort: BodyMeasurementRepositoryPort,
) {
    @Suppress("LongParameterList", "CyclomaticComplexMethod", "TooGenericExceptionCaught", "ReturnCount")
    suspend operator fun invoke(
        weightKg: Double,
        chestCm: Double? = null,
        waistCm: Double? = null,
        hipCm: Double? = null,
        bicepLeftCm: Double? = null,
        bicepRightCm: Double? = null,
        thighLeftCm: Double? = null,
        thighRightCm: Double? = null,
        notes: String? = null,
        id: String? = null,
    ): Result<Unit> {
        if (weightKg <= 0) {
            return Result.failure(IllegalArgumentException("Weight must be greater than 0"))
        }
        if (chestCm != null && chestCm <= 0) {
            return Result.failure(IllegalArgumentException("Chest measurement must be greater than 0"))
        }
        if (waistCm != null && waistCm <= 0) {
            return Result.failure(IllegalArgumentException("Waist measurement must be greater than 0"))
        }
        if (hipCm != null && hipCm <= 0) {
            return Result.failure(IllegalArgumentException("Hip measurement must be greater than 0"))
        }
        if (bicepLeftCm != null && bicepLeftCm <= 0) {
            return Result.failure(IllegalArgumentException("Left bicep measurement must be greater than 0"))
        }
        if (bicepRightCm != null && bicepRightCm <= 0) {
            return Result.failure(IllegalArgumentException("Right bicep measurement must be greater than 0"))
        }
        if (thighLeftCm != null && thighLeftCm <= 0) {
            return Result.failure(IllegalArgumentException("Left thigh measurement must be greater than 0"))
        }
        if (thighRightCm != null && thighRightCm <= 0) {
            return Result.failure(IllegalArgumentException("Right thigh measurement must be greater than 0"))
        }

        val now = System.currentTimeMillis()
        val measurement =
            BodyMeasurement(
                id = id ?: java.util.UUID.randomUUID().toString(),
                weightKg = weightKg,
                chestCm = chestCm,
                waistCm = waistCm,
                hipCm = hipCm,
                bicepLeftCm = bicepLeftCm,
                bicepRightCm = bicepRightCm,
                thighLeftCm = thighLeftCm,
                thighRightCm = thighRightCm,
                notes = notes,
                createdAt = now,
                updatedAt = now,
            )

        return try {
            repositoryPort.saveMeasurement(measurement)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
