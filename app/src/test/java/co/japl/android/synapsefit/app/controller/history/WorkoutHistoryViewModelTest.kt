package co.japl.android.synapsefit.app.controller.history

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetWorkoutHistorySummaryUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutHistoryViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadHistory_updatesStateWithSummaries() =
        runTest {
            val logRepository = mockk<WorkoutLogRepositoryPort>()
            val planRepository = mockk<WorkoutPlanRepositoryPort>()

            val summaries =
                listOf(
                    WorkoutSummaryItem(
                        sessionId = "plan1_1_2023-10-05",
                        sessionTitle = "Plan 1 - Día 1",
                        avgWeightKg = 65.5,
                        totalDurationSeconds = 3600L,
                        dateIso = "2023-10-05",
                        day = 1,
                        totalExercisesCount = 5,
                        timestamp = 1696500000000L,
                    ),
                )
            val plan =
                WorkoutPlan(
                    id = "plan1",
                    title = "Plan 1",
                    goalDescription = "Goal",
                    createdAt = 0L,
                    updatedAt = 0L,
                    isActive = true,
                )

            every { logRepository.getWorkoutSummaries() } returns flowOf(summaries)
            every { planRepository.getAllPlans() } returns flowOf(listOf(plan))

            val useCase = GetWorkoutHistorySummaryUseCase(logRepository)
            val viewModel =
                WorkoutHistoryViewModel(
                    workoutPlanRepositoryPort = planRepository,
                    getWorkoutHistorySummaryUseCase = useCase,
                )

            val state = viewModel.uiState.value
            assertEquals(1, state.summaryItems.size)
            assertEquals("Plan 1 - Día 1", state.summaryItems.first().sessionTitle)
            assertEquals(65.5, state.summaryItems.first().avgWeightKg, 0.01)
            assertEquals(5, state.summaryItems.first().totalExercisesCount)
        }
}
