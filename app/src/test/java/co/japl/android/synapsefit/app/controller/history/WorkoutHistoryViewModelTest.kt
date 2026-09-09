package co.japl.android.synapsefit.app.controller.history

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.history.ExerciseHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutHistoryViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase = mockk()
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadHistory_populatesDashboardStateCorrectly() =
        runTest {
            val now = System.currentTimeMillis()
            val group =
                WorkoutHistoryGroup(
                    sessionId = "plan1_1_$now",
                    planId = "plan1",
                    planTitle = "Plan Hipertrofia",
                    day = 1,
                    timestamp = now,
                    exercises =
                        listOf(
                            ExerciseHistory(
                                exerciseId = "ex1",
                                exerciseName = "Press de Banca",
                                muscleGroup = "Pecho",
                                sets =
                                    listOf(
                                        ExerciseSetHistory(1, 10, 50.0, 120, 60L, now),
                                        ExerciseSetHistory(2, 10, 60.0, 125, 60L, now),
                                    ),
                                averageReps = 10.0,
                                averageWeightKg = 55.0,
                            ),
                        ),
                    totalVolumeKg = 1100.0,
                    totalDurationSeconds = 120L,
                    muscleGroups = listOf("Pecho"),
                )
            val plan = WorkoutPlan("plan1", "Plan Hipertrofia", "Ganar músculo", true, true, 12, now, now)

            every { getGroupedWorkoutHistoryUseCase.invoke() } returns flowOf(listOf(group))
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))

            val viewModel = WorkoutHistoryViewModel(workoutPlanRepositoryPort, getGroupedWorkoutHistoryUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("Plan Hipertrofia", state.activePlanStats.planTitle)
            assertEquals(1, state.sessionGroups.size)
            assertEquals(1, state.weeklySessionsCount)
            assertEquals(1100.0, state.weeklyTotalVolumeKg, 0.1)
        }

    @Test
    fun monthNavigation_updatesSelectedMonthAndGrid() =
        runTest {
            every { getGroupedWorkoutHistoryUseCase.invoke() } returns flowOf(emptyList())
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(emptyList())

            val viewModel = WorkoutHistoryViewModel(workoutPlanRepositoryPort, getGroupedWorkoutHistoryUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            val initialMonth = viewModel.uiState.value.selectedYearMonth
            viewModel.selectPreviousMonth()
            testDispatcher.scheduler.advanceUntilIdle()

            val prevMonth = viewModel.uiState.value.selectedYearMonth
            assertTrue(prevMonth != initialMonth)

            viewModel.selectNextMonth()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(initialMonth, viewModel.uiState.value.selectedYearMonth)
        }
}
