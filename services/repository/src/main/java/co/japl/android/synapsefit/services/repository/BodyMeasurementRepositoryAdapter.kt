package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.services.database.dao.BodyMeasurementDao
import co.japl.android.synapsefit.services.database.mapper.toDomain
import co.japl.android.synapsefit.services.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BodyMeasurementRepositoryAdapter(
    private val dao: BodyMeasurementDao,
) : BodyMeasurementRepositoryPort {
    override fun getLatestMeasurement(): Flow<BodyMeasurement?> =
        dao.getLatestMeasurement().map { entity ->
            entity?.toDomain()
        }

    override fun getMeasurementsHistory(): Flow<List<BodyMeasurement>> =
        dao.getMeasurementsHistory().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveMeasurement(measurement: BodyMeasurement) {
        dao.insertMeasurement(measurement.toEntity())
    }

    override suspend fun deleteMeasurement(id: String) {
        dao.deleteMeasurement(id)
    }
}
