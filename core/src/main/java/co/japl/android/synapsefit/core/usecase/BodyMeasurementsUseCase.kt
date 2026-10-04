package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import kotlinx.coroutines.flow.Flow

class BodyMeasurementsUseCase(
    private val bodyMeasurementRepository: BodyMeasurementRepositoryPort,
) : IBodyMeasurementsUseCase {
    private val saveUseCase = SaveBodyMeasurementUseCase(bodyMeasurementRepository)

    override fun getLatestMeasurement(): Flow<BodyMeasurement?> = bodyMeasurementRepository.getLatestMeasurement()

    override suspend fun saveMeasurement(measurement: BodyMeasurement) {
        saveUseCase(
            weightKg = measurement.weightKg,
            chestCm = measurement.chestCm,
            waistCm = measurement.waistCm,
            hipCm = measurement.hipCm,
        )
    }
}
