package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

interface IMeasurementProgressGraphUseCase {
    fun getMeasurementHistory(): Flow<List<BodyMeasurement>>
}
