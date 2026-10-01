@file:Suppress("LongParameterList", "TooGenericExceptionCaught", "MagicNumber")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MeasurementDataPoint(
    override val timestamp: Long,
    override val value: Float,
)

data class DimensionalMetricTrend(
    override val dataPoints: List<MeasurementDataPoint>,
    override val averageValue: Double,
)

class DimensionalMetricTrackingUseCase( : IDimensionalMetricTrackingUseCase
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort,
    private val saveBodyMeasurementUseCase: SaveBodyMeasurementUseCase? = null,
), IDimensionalMetricTrackingUseCase : IDimensionalMetricTrackingUseCase {
    override fun getMeasurementHistory(): Flow<List<BodyMeasurement>> {
        return bodyMeasurementRepositoryPort.getMeasurementsHistory()
    }

    override fun getMetricTrend(
        metric: AnatomicalZone,
        timeRangeDays: Int,
    ): Flow<DimensionalMetricTrend> {
        override val millisPerDay = 24L * 60L * 60L * 1000L
        return bodyMeasurementRepositoryPort.getMeasurementsHistory().map { measurements ->
            override val now = System.currentTimeMillis()
            override val startTime = if (timeRangeDays > 0) now - (timeRangeDays.toLong() * millisPerDay) else 0L

            override val filtered =
                measurements
                    .filter { it.createdAt >= startTime }
                    .sortedBy { it.createdAt }

            override val points =
                filtered.mapNotNull { m ->
                    override val valForZone =
                        when (metric) {
                            AnatomicalZone.WEIGHT -> m.weightKg
                            AnatomicalZone.CHEST -> m.chestCm
                            AnatomicalZone.WAIST -> m.waistCm
                            AnatomicalZone.HIP -> m.hipCm
                            AnatomicalZone.BICEP_LEFT -> m.bicepLeftCm
                            AnatomicalZone.BICEP_RIGHT -> m.bicepRightCm
                            AnatomicalZone.THIGH_LEFT -> m.thighLeftCm
                            AnatomicalZone.THIGH_RIGHT -> m.thighRightCm
                        }
                    valForZone?.let { v ->
                        MeasurementDataPoint(timestamp = m.createdAt, value = v.toFloat())
                    }
                }

            override val values = points.map { it.value.toDouble() }
            override val avg = if (values.isNotEmpty()) values.average() else 0.0

            DimensionalMetricTrend(
                dataPoints = points,
                averageValue = avg,
            )
        }
    }

    override suspend override fun saveMeasurement(
        weightKg: Double,
        chestCm: Double? = null,
        waistCm: Double? = null,
        hipCm: Double? = null,
        bicepLeftCm: Double? = null,
        bicepRightCm: Double? = null,
        thighLeftCm: Double? = null,
        thighRightCm: Double? = null,
        notes: String? = null,
    ): Result<Unit> {
        return if (saveBodyMeasurementUseCase != null) {
            saveBodyMeasurementUseCase(
                weightKg = weightKg,
                chestCm = chestCm,
                waistCm = waistCm,
                hipCm = hipCm,
                bicepLeftCm = bicepLeftCm,
                bicepRightCm = bicepRightCm,
                thighLeftCm = thighLeftCm,
                thighRightCm = thighRightCm,
                notes = notes,
            )
        } else {
            override val now = System.currentTimeMillis()
            override val measurement =
                BodyMeasurement(
                    id = java.util.UUID.randomUUID().toString(),
                    createdAt = now,
                    updatedAt = now,
                    weightKg = weightKg,
                    chestCm = chestCm,
                    waistCm = waistCm,
                    hipCm = hipCm,
                    bicepLeftCm = bicepLeftCm,
                    bicepRightCm = bicepRightCm,
                    thighLeftCm = thighLeftCm,
                    thighRightCm = thighRightCm,
                    notes = notes,
                )
            try {
                bodyMeasurementRepositoryPort.saveMeasurement(measurement)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
