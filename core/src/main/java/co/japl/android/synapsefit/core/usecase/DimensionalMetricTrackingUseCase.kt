@file:Suppress("LongParameterList", "TooGenericExceptionCaught", "MagicNumber")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val HOURS_PER_DAY = 24L
private const val MINUTES_PER_HOUR = 60L
private const val SECONDS_PER_MINUTE = 60L
private const val MILLIS_PER_SECOND = 1000L
private const val MILLIS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND

class DimensionalMetricTrackingUseCase(
    private val bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort,
    private val saveBodyMeasurementUseCase: SaveBodyMeasurementUseCase? = null,
) : IDimensionalMetricTrackingUseCase {
    override fun getMeasurementHistory(): Flow<List<BodyMeasurement>> {
        return bodyMeasurementRepositoryPort.getMeasurementsHistory()
    }

    override fun getMetricTrend(
        metric: AnatomicalZone,
        timeRangeDays: Int,
    ): Flow<DimensionalMetricTrend> {
        return bodyMeasurementRepositoryPort.getMeasurementsHistory().map { measurements ->
            val now = System.currentTimeMillis()
            val startTime = if (timeRangeDays > 0) now - (timeRangeDays.toLong() * MILLIS_PER_DAY) else 0L

            val filtered =
                measurements
                    .filter { it.createdAt >= startTime }
                    .sortedBy { it.createdAt }

            val points =
                filtered.mapNotNull { m ->
                    val valForZone =
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

            val values = points.map { it.value.toDouble() }
            val avg = if (values.isNotEmpty()) values.average() else 0.0

            DimensionalMetricTrend(
                dataPoints = points,
                averageValue = avg,
            )
        }
    }

    @Suppress("LongParameterList", "TooGenericExceptionCaught")
    override suspend fun saveMeasurement(
        weightKg: Double,
        chestCm: Double?,
        waistCm: Double?,
        hipCm: Double?,
        bicepLeftCm: Double?,
        bicepRightCm: Double?,
        thighLeftCm: Double?,
        thighRightCm: Double?,
        notes: String?,
    ): Result<Unit> {
        return if (saveBodyMeasurementUseCase != null) {
            saveBodyMeasurementUseCase(
                id = null,
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
            val now = System.currentTimeMillis()
            val measurement =
                BodyMeasurement(
                    id = java.util.UUID.randomUUID().toString(),
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
            try {
                bodyMeasurementRepositoryPort.saveMeasurement(measurement)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
