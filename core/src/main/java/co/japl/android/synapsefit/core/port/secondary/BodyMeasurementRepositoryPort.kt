package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepositoryPort {
    fun getLatestMeasurement(): Flow<BodyMeasurement?>
    fun getMeasurementsHistory(): Flow<List<BodyMeasurement>>
    suspend fun saveMeasurement(measurement: BodyMeasurement)
    suspend fun deleteMeasurement(id: String)
}
