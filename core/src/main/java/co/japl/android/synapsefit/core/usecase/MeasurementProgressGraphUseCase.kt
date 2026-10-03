package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import kotlinx.coroutines.flow.Flow

class MeasurementProgressGraphUseCase(
    private val bodyMeasurementRepository: BodyMeasurementRepositoryPort,
) : IMeasurementProgressGraphUseCase {
    override fun getMeasurementHistory(): Flow<List<BodyMeasurement>> = bodyMeasurementRepository.getMeasurementsHistory()
}
