package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

data class MeasurementDataPoint(
    val timestamp: Long,
    val value: Float,
)

data class DimensionalMetricTrend(
    val dataPoints: List<MeasurementDataPoint>,
    val averageValue: Double,
)

interface IDimensionalMetricTrackingUseCase {
    fun getMeasurementHistory(): Flow<List<BodyMeasurement>>

    fun getMetricTrend(
        metric: AnatomicalZone,
        timeRangeDays: Int,
    ): Flow<DimensionalMetricTrend>

    @Suppress("LongParameterList")
    suspend fun saveMeasurement(
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
