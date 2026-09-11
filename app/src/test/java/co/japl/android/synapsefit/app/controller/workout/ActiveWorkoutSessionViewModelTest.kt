@file:Suppress("MaxLineLength", "EmptyFunctionBlock")

package co.japl.android.synapsefit.app.controller.workout

import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeWearStateMirrorPort : WearStateMirrorPort {
    private val eventsFlow = MutableSharedFlow<LiveSyncEvent>(extraBufferCapacity = 64)
    override val liveSyncEvents: SharedFlow<LiveSyncEvent> = eventsFlow.asSharedFlow()
    override val isConnected: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun sendEvent(event: LiveSyncEvent) {
        eventsFlow.emit(event)
    }

    override fun onEventReceived(event: LiveSyncEvent) {
        eventsFlow.tryEmit(event)
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {
        // No-op
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutSessionViewModelTest {
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
        val isCooldownOrReady =
            state.stepState is TrainingStepState.Cooldown ||
                state.stepState is TrainingStepState.ReadyForNext
        assertTrue(isCooldownOrReady)
        viewModel.nextSetOrExercise()
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

    @Test
    fun testLiveSyncEvent_completeSet_startsCooldownLocally() =
        runTest {
            val mirrorAdapter = FakeWearStateMirrorPort()
            val viewModel = ActiveWorkoutSessionViewModel(null, null, null, null, null, mirrorAdapter)

            val targetTs = System.currentTimeMillis() + 60000L
            mirrorAdapter.onEventReceived(
                LiveSyncEvent.CompleteSet(
                    exerciseId = "ex1",
                    setIndex = 1,
                    reps = 10,
                    weightKg = 50.0,
                    targetTimestamp = targetTs,
                    cooldownDurationSeconds = 60,
                ),
            )

            val state = viewModel.uiState.value
            val isCooldownOrReady =
                state.stepState is TrainingStepState.Cooldown ||
                    state.stepState is TrainingStepState.ReadyForNext
            assertTrue(isCooldownOrReady)
            viewModel.nextSetOrExercise()
        }
}
