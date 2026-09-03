package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateActivePlanSessionsUseCaseTest {
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk()
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort = mockk()

    private lateinit var useCase: ValidateActivePlanSessionsUseCase

    @Before
    fun setUp() {
        useCase = ValidateActivePlanSessionsUseCase(workoutPlanRepositoryPort, workoutLogRepositoryPort)
    }

    @Test
    fun `when limit reached, return isLimitReached true`() =
        runTest {
            val plan =
                WorkoutPlan(
                    id = "p1",
                    title = "Plan 1",
                    goalDescription = "Goal",
                    isActive = true,
                    generatedByLlm = true,
                    totalSessions = 2,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            every { workoutPlanRepositoryPort.getActivePlan() } returns flowOf(plan)

            val logs =
                listOf(
                    WorkoutLog("l1", "e1", 10, 50.0, null, SourceDevice.MOBILE, 1700000000000L, 1000L, 1000L),
                    WorkoutLog("l2", "e1", 10, 50.0, null, SourceDevice.MOBILE, 1700100000000L, 1000L, 1000L),
                )
            every { workoutLogRepositoryPort.getLatestLogsForPlan("p1") } returns flowOf(logs)

            val result = useCase()
            assertNotNull(result)
            assertEquals(2, result?.completedSessionsCount)
            assertTrue(result!!.isLimitReached)
        }

    @Test
    fun `when limit not reached, return isLimitReached false`() =
        runTest {
            val plan =
                WorkoutPlan(
                    id = "p1",
                    title = "Plan 1",
                    goalDescription = "Goal",
                    isActive = true,
                    generatedByLlm = true,
                    totalSessions = 12,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            every { workoutPlanRepositoryPort.getActivePlan() } returns flowOf(plan)

            val logs =
                listOf(
                    WorkoutLog("l1", "e1", 10, 50.0, null, SourceDevice.MOBILE, 1700000000000L, 1000L, 1000L),
                )
            every { workoutLogRepositoryPort.getLatestLogsForPlan("p1") } returns flowOf(logs)

            val result = useCase()
            assertNotNull(result)
            assertEquals(1, result?.completedSessionsCount)
            assertFalse(result!!.isLimitReached)
        }
}
