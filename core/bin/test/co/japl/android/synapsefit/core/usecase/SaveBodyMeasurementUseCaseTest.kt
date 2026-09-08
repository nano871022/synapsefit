package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveBodyMeasurementUseCaseTest {
    private val repositoryPort: BodyMeasurementRepositoryPort = mockk(relaxed = true)
    private lateinit var useCase: SaveBodyMeasurementUseCase

    @Before
    fun setUp() {
        useCase = SaveBodyMeasurementUseCase(repositoryPort)
    }

    @Test
    fun `when weight is zero or negative, return failure result`() =
        runTest {
            val result = useCase(weightKg = 0.0)
            assertTrue(result.isFailure)
            assertEquals("Weight must be greater than 0", result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { repositoryPort.saveMeasurement(any()) }
        }

    @Test
    fun `when chest measurement is negative, return failure result`() =
        runTest {
            val result = useCase(weightKg = 70.0, chestCm = -10.0)
            assertTrue(result.isFailure)
            assertEquals("Chest measurement must be greater than 0", result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { repositoryPort.saveMeasurement(any()) }
        }

    @Test
    fun `when waist measurement is negative, return failure result`() =
        runTest {
            val result = useCase(weightKg = 70.0, waistCm = -5.0)
            assertTrue(result.isFailure)
            assertEquals("Waist measurement must be greater than 0", result.exceptionOrNull()?.message)
            coVerify(exactly = 0) { repositoryPort.saveMeasurement(any()) }
        }

    @Test
    fun `when parameters are valid, save measurement and return success`() =
        runTest {
            coEvery { repositoryPort.saveMeasurement(any()) } returns Unit

            val result =
                useCase(
                    weightKg = 75.5,
                    chestCm = 100.0,
                    waistCm = 82.0,
                    notes = "Morning measurement",
                )

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                repositoryPort.saveMeasurement(
                    match {
                        it.weightKg == 75.5 &&
                            it.chestCm == 100.0 &&
                            it.waistCm == 82.0 &&
                            it.notes == "Morning measurement"
                    },
                )
            }
        }
}
