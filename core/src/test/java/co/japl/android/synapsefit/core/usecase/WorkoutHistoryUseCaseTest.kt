package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WorkoutHistoryUseCaseTest {

    private lateinit var workoutLogRepositoryPort: WorkoutLogRepositoryPort
    private lateinit var workoutPlanRepositoryPort: WorkoutPlanRepositoryPort
    private lateinit var useCase: WorkoutHistoryUseCase

    @Before
    fun setUp() {
        workoutLogRepositoryPort = mockk()
        workoutPlanRepositoryPort = mockk()
        useCase = WorkoutHistoryUseCase(workoutLogRepositoryPort, workoutPlanRepositoryPort)
    }

    @Test
    fun testInvokeReturnsCombinedData() = runBlocking {
        val summaryItem = WorkoutSummaryItem(
            sessionId = "s1",
            sessionTitle = "Session 1",
            avgWeightKg = 50.0,
            totalDurationSeconds = 1800L,
            dateIso = "2025-01-01",
            day = 1,
            totalExercisesCount = 3,
            timestamp = 1000L,
        )
        val now = System.currentTimeMillis()
        val plan = WorkoutPlan(
            id = "p1",
            title = "Hypertrophy Plan",
            goalDescription = "Desc",
            isActive = true,
            generatedByLlm = false,
            totalSessions = 16,
            createdAt = now,
            updatedAt = now,
        )

        every { workoutLogRepositoryPort.getWorkoutSummaries() } returns flowOf(listOf(summaryItem))
        every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))

        val result = useCase().first()

        assertEquals(1, result.summaries.size)
        assertEquals("s1", result.summaries[0].sessionId)
        assertEquals("Hypertrophy Plan", result.activePlanTitle)
        assertEquals(16, result.activePlanTotalSessions)
    }
}
