package co.japl.android.synapsefit.app.controller.history

import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryRecord
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
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
    private val workoutLogRepositoryPort = mockk<WorkoutLogRepositoryPort>()
    private val workoutPlanRepositoryPort = mockk<WorkoutPlanRepositoryPort>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHistory updates state with history records and plans`() =
        runTest {
            val now = System.currentTimeMillis()
            val records =
                listOf(
                    WorkoutHistoryRecord(
                        logId = "1",
                        exerciseId = "ex1",
                        planId = "plan1",
                        planTitle = "Plan Hipertrofia",
                        day = 1,
                        exerciseName = "Ex 1",
                        muscleGroup = "Pecho",
                        repsCompleted = 10,
                        weightLiftedKg = 50.0,
                        heartRateBpm = 120,
                        durationSeconds = 60L,
                        sourceDevice = SourceDevice.MOBILE,
                        timestamp = now,
                    ),
                    WorkoutHistoryRecord(
                        logId = "2",
                        exerciseId = "ex1",
                        planId = "plan1",
                        planTitle = "Plan Hipertrofia",
                        day = 1,
                        exerciseName = "Ex 1",
                        muscleGroup = "Pecho",
                        repsCompleted = 10,
                        weightLiftedKg = 60.0,
                        heartRateBpm = 125,
                        durationSeconds = 60L,
                        sourceDevice = SourceDevice.MOBILE,
                        timestamp = now,
                    ),
            val plan = WorkoutPlan("plan1", "Plan Hipertrofia", "Ganar músculo", true, true, 12, now, now)

            every { workoutLogRepositoryPort.getHistoryRecords() } returns flowOf(records)
            every { workoutPlanRepositoryPort.getAllPlans() } returns flowOf(listOf(plan))

            val getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepositoryPort)
            val viewModel =
                WorkoutHistoryViewModel(
                    getGroupedWorkoutHistoryUseCase = getGroupedWorkoutHistoryUseCase,
                    workoutPlanRepositoryPort = workoutPlanRepositoryPort,
                )

            val state = viewModel.uiState.value

            assertEquals(false, state.isLoading)
            assertEquals(1, state.sessionGroups.size)
            assertEquals(1, state.sessionGroups.size)
        }
}
