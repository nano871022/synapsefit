package co.japl.android.synapsefit.viewmodel

import co.japl.android.synapsefit.core.domain.model.SyncStepState
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.usecase.PerformWearSyncUseCase
import co.japl.android.synapsefit.ui.viewmodel.WearSyncViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class WearSyncViewModelTest {
    private val performWearSyncUseCase: PerformWearSyncUseCase = mockk()
    private val syncPort: WearSyncPort = mockk(relaxed = true)

    private val isPhoneConnectedFlow = MutableStateFlow(true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: WearSyncViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { syncPort.isPhoneConnected } returns isPhoneConnectedFlow
        every { performWearSyncUseCase.invoke(any()) } returns
            flowOf(
                SyncStepState.DownloadingPlans,
                SyncStepState.ConfiguringNextPlan,
                SyncStepState.Finished(activeSessionDetected = false),
            )

        viewModel =
            WearSyncViewModel(
                performWearSyncUseCase = performWearSyncUseCase,
                syncPort = syncPort,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testStartSyncRunsUseCaseAndUpdatesState() =
        runTest {
            viewModel.startSync(isPostWorkout = false)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.isFinished)
            assertFalse(state.isDisconnected)
            assertEquals(SyncStepState.Finished(activeSessionDetected = false), state.stepState)
        }

    @Test
    fun testConnectionLossSetsDisconnectedState() =
        runTest {
            val syncFlow = MutableSharedFlow<SyncStepState>()
            every { performWearSyncUseCase.invoke(any()) } returns syncFlow

            viewModel.startSync(isPostWorkout = false)
            advanceUntilIdle()

            syncFlow.emit(SyncStepState.DownloadingPlans)
            advanceUntilIdle()

            isPhoneConnectedFlow.value = false
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.isDisconnected)
        }

    @Test
    fun testCancelSyncFinishesFlow() =
        runTest {
            viewModel.startSync(isPostWorkout = false)
            viewModel.cancelSync()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.isFinished)
            assertFalse(state.isDisconnected)
        }
}
