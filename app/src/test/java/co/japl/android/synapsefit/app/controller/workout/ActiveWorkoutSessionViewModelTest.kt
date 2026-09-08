package co.japl.android.synapsefit.app.controller.workout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveWorkoutSessionViewModelTest {
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
