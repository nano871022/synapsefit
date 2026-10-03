package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

interface IBodyMeasurementsUseCase {
    fun getLatestMeasurement(): Flow<BodyMeasurement?>
    suspend fun saveMeasurement(measurement: BodyMeasurement)
}
