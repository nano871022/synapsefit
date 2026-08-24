package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecordWorkoutSessionUseCaseTest {

    private val repositoryPort: WorkoutLogRepositoryPort = mockk(relaxed = true)
    private lateinit var useCase: RecordWorkoutSessionUseCase

    @Before
    fun setUp() {
        useCase = RecordWorkoutSessionUseCase(repositoryPort)
    }

    @Test
    fun `when exerciseId is empty, return failure`() = runTest {
        val result = useCase(exerciseId = "", repsCompleted = 10, weightLiftedKg = 20.0)
        assertTrue(result.isFailure)
        assertEquals("Exercise ID cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when repsCompleted is zero or negative, return failure`() = runTest {
        val result = useCase(exerciseId = "ex1", repsCompleted = 0, weightLiftedKg = 20.0)
        assertTrue(result.isFailure)
        assertEquals("Reps completed must be greater than 0", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when weightLiftedKg is negative, return failure`() = runTest {
        val result = useCase(exerciseId = "ex1", repsCompleted = 10, weightLiftedKg = -5.0)
        assertTrue(result.isFailure)
        assertEquals("Weight lifted cannot be negative", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when parameters valid, save log and return log`() = runTest {
        coEvery { repositoryPort.saveLog(any()) } returns Unit

        val result = useCase(
            exerciseId = "ex1",
            repsCompleted = 12,
            weightLiftedKg = 50.0,
            heartRateBpm = 135,
            sourceDevice = SourceDevice.WEAR_OS
        )

        assertTrue(result.isSuccess)
        val log = result.getOrThrow()
        assertEquals("ex1", log.exerciseId)
        assertEquals(12, log.repsCompleted)
        assertEquals(50.0, log.weightLiftedKg, 0.001)
        assertEquals(135, log.heartRateBpm)
        assertEquals(SourceDevice.WEAR_OS, log.sourceDevice)
        coVerify(exactly = 1) { repositoryPort.saveLog(log) }
    }
}
