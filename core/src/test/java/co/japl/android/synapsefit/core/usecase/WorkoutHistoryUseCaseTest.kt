package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WorkoutHistoryUseCaseTest {

    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort = mockk()
    private lateinit var useCase: WorkoutHistoryUseCase

    @Before
    fun setUp() {
        useCase = WorkoutHistoryUseCase(workoutLogRepositoryPort)
    }

    @Test
    fun `getGroupedHistory delegates to getGroupedWorkoutHistoryUseCase`() = runTest {
        every { workoutLogRepositoryPort.getHistoryRecords() } returns flowOf(emptyList())

        val result = useCase.getGroupedHistory().first()

        assertEquals(0, result.size)
    }

    @Test
    fun `getHistorySummary delegates to getWorkoutHistorySummaryUseCase`() = runTest {
        val summary = listOf(
            WorkoutSummaryItem(
                sessionId = "s1",
                sessionTitle = "Session 1",
                day = 1,
                dateIso = "2023-10-10",
                timestamp = 0L,
                avgWeightKg = 10.0,
                totalDurationSeconds = 0L,
                totalExercisesCount = 1,
            )
        )
        every { workoutLogRepositoryPort.getWorkoutSummaries() } returns flowOf(summary)

        val result = useCase.getHistorySummary().first()

        assertEquals(1, result.size)
        assertEquals("s1", result.first().sessionId)
    }
}
