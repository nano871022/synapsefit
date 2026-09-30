package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailRecord
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WorkoutDetailUseCaseTest {

    private lateinit var workoutLogRepositoryPort: WorkoutLogRepositoryPort
    private lateinit var useCase: GetWorkoutDetailUseCase

    @Before
    fun setUp() {
        workoutLogRepositoryPort = mockk()
        useCase = GetWorkoutDetailUseCase(workoutLogRepositoryPort)
    }

    @Test
    fun testInvokeWithEmptyRecords() = runBlocking {
        every { workoutLogRepositoryPort.getWorkoutDetailRecords("2025-01-01", 1) } returns flowOf(emptyList())

        val result = useCase("2025-01-01", 1).first()
        assertNull(result)
    }

    @Test
    fun testInvokeWithRecords() = runBlocking {
        val record = WorkoutDetailRecord(
            logId = "l1",
            exerciseId = "e1",
            planId = "p1",
            planTitle = "Plan 1",
            day = 1,
            exerciseName = "Bench Press",
            exerciseDetail = "Detail",
            muscleGroup = "Chest",
            repsCompleted = 10,
            weightLiftedKg = 80.0,
            heartRateBpm = 120,
            durationSeconds = 60L,
            sourceDevice = SourceDevice.MOBILE,
            timestamp = 1000L,
            dateIso = "2025-01-01",
        )

        every { workoutLogRepositoryPort.getWorkoutDetailRecords("2025-01-01", 1) } returns flowOf(listOf(record))

        val result = useCase("2025-01-01", 1).first()
        assertNotNull(result)
        assertEquals("p1", result?.planId)
        assertEquals("Plan 1", result?.planTitle)
        assertEquals(1, result?.exercises?.size)
        assertEquals("Bench Press", result?.exercises?.get(0)?.exerciseName)
    }
}
