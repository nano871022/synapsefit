package co.japl.android.synapsefit.app.controller.workout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutSessionViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_isTrainingStepActive() {
        val viewModel = ActiveWorkoutSessionViewModel()
        val state = viewModel.uiState.value

        assertEquals(TrainingStepState.Active, state.stepState)
        assertEquals(null, state.cooldownTargetTimestamp)
    }

    @Test
    fun testCompleteSet_initiatesCooldownState() {
        val viewModel = ActiveWorkoutSessionViewModel()

        viewModel.startRestTimer(cooldownDurationSeconds = 60)
        testDispatcher.scheduler.advanceTimeBy(100L)

        val state = viewModel.uiState.value
        assertTrue(state.stepState is TrainingStepState.Cooldown || state.stepState is TrainingStepState.ReadyForNext)
    }

    @Test
    fun testNextSet_resetsToActiveState() {
        val viewModel = ActiveWorkoutSessionViewModel()

        viewModel.startRestTimer(cooldownDurationSeconds = 60)
        viewModel.nextSetOrExercise()

        val state = viewModel.uiState.value
        assertEquals(TrainingStepState.Active, state.stepState)
        assertEquals(null, state.cooldownTargetTimestamp)
    }
}
