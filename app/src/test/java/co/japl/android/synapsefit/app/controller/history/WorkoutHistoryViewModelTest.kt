package co.japl.android.synapsefit.app.controller.history

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
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
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort = mockk()
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
            val logs =
                listOf(
                    WorkoutLog("1", "ex1", 10, 50.0, 120, SourceDevice.MOBILE, 60L, now, now, now),
                    WorkoutLog("2", "ex1", 10, 60.0, 125, SourceDevice.MOBILE, 60L, now, now, now),
                )
            val plan = WorkoutPlan("plan1", "Plan Hipertrofia", "Ganar músculo", true, true, 12, now, now)

            every { workoutLogRepositoryPort.getAllLogs() } returns flowOf(logs)
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))

            val getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepositoryPort)
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
            every { workoutLogRepositoryPort.getAllLogs() } returns flowOf(emptyList())
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(emptyList())

            val getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepositoryPort)
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
