package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DimensionalMetricTrackingUseCaseTest {

    private lateinit var bodyMeasurementRepositoryPort: BodyMeasurementRepositoryPort
    private lateinit var useCase: DimensionalMetricTrackingUseCase

    @Before
    fun setUp() {
        bodyMeasurementRepositoryPort = mockk(relaxed = true)
        useCase = DimensionalMetricTrackingUseCase(bodyMeasurementRepositoryPort)
    }

    @Test
    fun testGetMeasurementHistory() = runBlocking {
        val now = System.currentTimeMillis()
        val measurement = BodyMeasurement(id = "m1", createdAt = now, updatedAt = now, weightKg = 75.0)
        every { bodyMeasurementRepositoryPort.getMeasurementsHistory() } returns flowOf(listOf(measurement))

        val result = useCase.getMeasurementHistory().first()
        assertEquals(1, result.size)
        assertEquals("m1", result[0].id)
    }

    @Test
    fun testGetMetricTrend() = runBlocking {
        val now = System.currentTimeMillis()
        val m1 = BodyMeasurement(id = "m1", createdAt = now - 1000, updatedAt = now, weightKg = 70.0)
        val m2 = BodyMeasurement(id = "m2", createdAt = now, updatedAt = now, weightKg = 80.0)
        every { bodyMeasurementRepositoryPort.getMeasurementsHistory() } returns flowOf(listOf(m1, m2))

        val trend = useCase.getMetricTrend(AnatomicalZone.WEIGHT, 30).first()
        assertEquals(2, trend.dataPoints.size)
        assertEquals(75.0, trend.averageValue, 0.01)
    }

    @Test
    fun testSaveMeasurement() = runBlocking {
        val result = useCase.saveMeasurement(weightKg = 72.5)
        assertTrue(result.isSuccess)
        coVerify { bodyMeasurementRepositoryPort.saveMeasurement(any()) }
    }
}
