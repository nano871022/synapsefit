package co.japl.android.synapsefit.viewmodel

import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.ui.viewmodel.FocusedInput
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WearActiveWorkoutViewModelTest {
    private val sensorAdapter: WearSensorPort = mockk(relaxed = true)
    private val syncAdapter: WearSyncPort = mockk(relaxed = true)

    private val heartRateFlow = MutableStateFlow(0)
    private val connectionStateFlow = MutableStateFlow(true)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: WearActiveWorkoutViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        io.mockk.every { sensorAdapter.heartRateBpm } returns heartRateFlow
        io.mockk.every { syncAdapter.isPhoneConnected } returns connectionStateFlow

        viewModel =
            WearActiveWorkoutViewModel(
                sensorPort = sensorAdapter,
                syncPort = syncAdapter,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialUiState() {
        val state = viewModel.uiState.value
        assertEquals("", state.exerciseName)
        assertEquals(0, state.currentHeartRateBpm)
        assertEquals(0, state.currentReps)
        assertEquals(0.toShort(), state.currentWeight)
        assertEquals(FocusedInput.REPS, state.focusedInput)
        assertFalse(state.isNumericKeypadOpen)
        assertTrue(state.isSyncedWithPhone)
    }

    @Test
    fun testSetFocusedInputChangesState() {
        viewModel.setFocusedInput(FocusedInput.WEIGHT)
        assertEquals(FocusedInput.WEIGHT, viewModel.uiState.value.focusedInput)

        viewModel.setFocusedInput(FocusedInput.REPS)
        assertEquals(FocusedInput.REPS, viewModel.uiState.value.focusedInput)
    }

    @Test
    fun testIncrementAndDecrementReps() {
        assertEquals(0, viewModel.uiState.value.currentReps)

        viewModel.incrementReps()
        assertEquals(1, viewModel.uiState.value.currentReps)

        viewModel.incrementReps()
        assertEquals(2, viewModel.uiState.value.currentReps)

        viewModel.decrementReps()
        assertEquals(1, viewModel.uiState.value.currentReps)

        viewModel.decrementReps()
        assertEquals(0, viewModel.uiState.value.currentReps)

        viewModel.decrementReps()
        assertEquals(0, viewModel.uiState.value.currentReps)
    }

    @Test
    fun testIncrementAndDecrementWeight() {
        assertEquals(0.toShort(), viewModel.uiState.value.currentWeight)

        viewModel.incrementWgt()
        assertEquals(1.toShort(), viewModel.uiState.value.currentWeight)

        viewModel.incrementWgt()
        assertEquals(2.toShort(), viewModel.uiState.value.currentWeight)

        viewModel.decrementWgt()
        assertEquals(1.toShort(), viewModel.uiState.value.currentWeight)

        viewModel.decrementWgt()
        assertEquals(0.toShort(), viewModel.uiState.value.currentWeight)

        viewModel.decrementWgt()
        assertEquals(0.toShort(), viewModel.uiState.value.currentWeight)
    }

    @Test
    fun testNumericKeypadOpenAndClose() {
        assertFalse(viewModel.uiState.value.isNumericKeypadOpen)

        viewModel.openNumericKeypad()
        assertTrue(viewModel.uiState.value.isNumericKeypadOpen)

        viewModel.closeNumericKeypad()
        assertFalse(viewModel.uiState.value.isNumericKeypadOpen)
    }

    @Test
    fun testSetFocusedValueDirectReps() {
        viewModel.setFocusedInput(FocusedInput.REPS)
        viewModel.openNumericKeypad()

        viewModel.setFocusedValueDirect(12)

        assertEquals(12, viewModel.uiState.value.currentReps)
        assertFalse(viewModel.uiState.value.isNumericKeypadOpen)
    }

    @Test
    fun testSetFocusedValueDirectWeight() {
        viewModel.setFocusedInput(FocusedInput.WEIGHT)
        viewModel.openNumericKeypad()

        viewModel.setFocusedValueDirect(45)

        assertEquals(45.toShort(), viewModel.uiState.value.currentWeight)
        assertFalse(viewModel.uiState.value.isNumericKeypadOpen)
    }

    @Test
    fun testUpdateExerciseNameUpdatesState() {
        viewModel.updateExerciseName("Press de Banca")
        assertEquals("Press de Banca", viewModel.uiState.value.exerciseName)
    }

    @Test
    fun testUpdateHeartRateUpdatesStateAndSensorPort() {
        viewModel.updateHeartRate(145)

        assertEquals(145, viewModel.uiState.value.currentHeartRateBpm)
        verify { sensorAdapter.onHeartRateSensorChanged(145) }
    }

    @Test
    fun testSetSyncStatusUpdatesStateAndSyncPort() {
        viewModel.setSyncStatus(false)

        assertFalse(viewModel.uiState.value.isSyncedWithPhone)
        verify { syncAdapter.onConnectionStateChanged(false) }
    }

    @Test
    fun testLoadExerciseSessionsSetsFirstExerciseActive() {
        val sessions =
            listOf(
                ExerciseSession(
                    exerciseId = "ex1",
                    planId = "p1",
                    name = "Sentadillas",
                    muscleGroup = "Piernas",
                    targetSets = 4,
                    targetReps = "10-12",
                    restSeconds = 90,
                ),
            )

        viewModel.loadExerciseSessions(sessions)

        assertEquals(1, viewModel.uiState.value.exerciseSessions.size)
        assertEquals("Sentadillas", viewModel.uiState.value.exerciseName)

        val stepState = viewModel.trainingStepState.value
        assertTrue(stepState is TrainingStepState.Active)
        val activeState = stepState as TrainingStepState.Active
        assertEquals("Sentadillas", activeState.exerciseSession.name)
        assertEquals(1, activeState.currentSet)
    }

    @Test
    fun testCompleteSetTransitionsToCooldownThenReadyForNext() {
        val sessions =
            listOf(
                ExerciseSession(
                    exerciseId = "ex1",
                    planId = "p1",
                    name = "Sentadillas",
                    muscleGroup = "Piernas",
                    targetSets = 1,
                    targetReps = "10",
                    restSeconds = 60,
                ),
                ExerciseSession(
                    exerciseId = "ex2",
                    planId = "p1",
                    name = "Press Militar",
                    muscleGroup = "Hombros",
                    targetSets = 1,
                    targetReps = "10",
                    restSeconds = 60,
                ),
            )

        viewModel.loadExerciseSessions(sessions)

        viewModel.completeSet()
        assertTrue(viewModel.trainingStepState.value is TrainingStepState.Cooldown)
        val cooldown = viewModel.trainingStepState.value as TrainingStepState.Cooldown
        assertEquals(1, cooldown.exerciseSession.completedSets)
        assertTrue(cooldown.exerciseSession.completedSets >= cooldown.exerciseSession.targetSets)

        viewModel.setCooldownTargetTimestamp(System.currentTimeMillis() - 1000L)
        viewModel.recalculateCooldownTimer()

        assertTrue(viewModel.trainingStepState.value is TrainingStepState.Active)
        val readyState = viewModel.trainingStepState.value as TrainingStepState.Active
        assertEquals("Press Militar", readyState.exerciseSession.name)

        viewModel.completeSet()
        assertTrue(viewModel.uiState.value.isRoutineCompleted)
    }
}
