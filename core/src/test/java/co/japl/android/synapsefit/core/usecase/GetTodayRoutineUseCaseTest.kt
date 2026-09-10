package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("LongMethod")
class GetTodayRoutineUseCaseTest {
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk()
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort = mockk()

    private lateinit var useCase: GetTodayRoutineUseCase

    @Before
    fun setUp() {
        useCase = GetTodayRoutineUseCase(workoutPlanRepositoryPort, workoutLogRepositoryPort)
    }

    @Test
    fun `when active plan exists and no log history, Day 1 is scheduled for today and sorted first`() =
        runTest {
            val plan =
                WorkoutPlan(
                    id = "p1",
                    title = "Hipertrofia",
                    goalDescription = "Ganar músculo",
                    isActive = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val exDay1 =
                Exercise(
                    id = "e1",
                    planId = "p1",
                    name = "Press de Banca",
                    muscleGroup = "Pecho",
                    targetSets = 3,
                    targetReps = "10",
                    restSeconds = 60,
                    day = 1,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val exDay2 =
                Exercise(
                    id = "e2",
                    planId = "p1",
                    name = "Sentadilla",
                    muscleGroup = "Pierna",
                    targetSets = 3,
                    targetReps = "10",
                    restSeconds = 60,
                    day = 2,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )

            every { workoutPlanRepositoryPort.getActivePlan() } returns flowOf(plan)
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))
            every {
                workoutPlanRepositoryPort.getPlanWithExercises("p1")
            } returns flowOf(Pair(plan, listOf(exDay1, exDay2)))
            every { workoutLogRepositoryPort.getLatestLogsForPlan("p1") } returns flowOf(emptyList())

            val items = useCase().first()

            assertEquals(2, items.size)
            assertTrue(items[0].isTodayScheduled)
            assertEquals(1, items[0].day)
            assertEquals("Hipertrofia", items[0].planTitle)
        }

    @Test
    fun `when active plan has log for Day 1, Day 2 is scheduled for today`() =
        runTest {
            val plan =
                WorkoutPlan(
                    id = "p1",
                    title = "Hipertrofia",
                    goalDescription = "Ganar músculo",
                    isActive = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val exDay1 =
                Exercise(
                    id = "e1",
                    planId = "p1",
                    name = "Press de Banca",
                    muscleGroup = "Pecho",
                    targetSets = 3,
                    targetReps = "10",
                    restSeconds = 60,
                    day = 1,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val exDay2 =
                Exercise(
                    id = "e2",
                    planId = "p1",
                    name = "Sentadilla",
                    muscleGroup = "Pierna",
                    targetSets = 3,
                    targetReps = "10",
                    restSeconds = 60,
                    day = 2,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )
            val logDay1 =
                WorkoutLog(
                    id = "l1",
                    exerciseId = "e1",
                    repsCompleted = 10,
                    weightLiftedKg = 60.0,
                    heartRateBpm = 120,
                    sourceDevice = SourceDevice.MOBILE,
                    durationSeconds = 45L,
                    timestamp = 2000L,
                    createdAt = 2000L,
                    updatedAt = 2000L,
                )

            every { workoutPlanRepositoryPort.getActivePlan() } returns flowOf(plan)
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))
            every {
                workoutPlanRepositoryPort.getPlanWithExercises("p1")
            } returns flowOf(Pair(plan, listOf(exDay1, exDay2)))
            every { workoutLogRepositoryPort.getLatestLogsForPlan("p1") } returns flowOf(listOf(logDay1))

            val items = useCase().first()

            assertEquals(2, items.size)
            assertTrue(items[0].isTodayScheduled)
            assertEquals(2, items[0].day)
        }

    @Test
    fun `when no plans exist, returns empty list`() =
        runTest {
            every { workoutPlanRepositoryPort.getActivePlan() } returns flowOf(null)
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(emptyList())

            val items = useCase().first()

            assertTrue(items.isEmpty())
        }
}
