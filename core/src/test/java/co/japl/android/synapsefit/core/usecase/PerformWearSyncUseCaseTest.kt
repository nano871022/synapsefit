package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.SyncStepState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerformWearSyncUseCaseTest {
    private val wearSyncPort: WearSyncPort = mockk(relaxed = true)
    private val wearStateMirrorPort: WearStateMirrorPort = mockk(relaxed = true)
    private val workoutPlanRepository: WorkoutPlanRepositoryPort = mockk(relaxed = true)
    private val workoutLogRepository: WorkoutLogRepositoryPort = mockk(relaxed = true)
    private val activeSessionRepository: ActiveSessionRepositoryPort = mockk(relaxed = true)

    private val pendingDataCountFlow = MutableStateFlow(0)
    private val liveSyncEventsFlow = MutableSharedFlow<LiveSyncEvent>()

    private lateinit var useCase: PerformWearSyncUseCase

    @Before
    fun setUp() {
        every { wearSyncPort.pendingSyncDataCount } returns pendingDataCountFlow
        every { wearStateMirrorPort.liveSyncEvents } returns liveSyncEventsFlow

        useCase =
            PerformWearSyncUseCase(
                wearSyncPort = wearSyncPort,
                wearStateMirrorPort = wearStateMirrorPort,
                workoutPlanRepository = workoutPlanRepository,
                workoutLogRepository = workoutLogRepository,
                activeSessionRepository = activeSessionRepository,
            )
    }

    @Test
    fun testInvokeEmitsSequentialSyncSteps() =
        runTest {
            pendingDataCountFlow.value = 2

            val steps = useCase(isPostWorkout = false).toList()

            assertTrue(steps.contains(SyncStepState.DownloadingPlans))
            assertTrue(steps.contains(SyncStepState.ConfiguringNextPlan))
            assertTrue(steps.contains(SyncStepState.VerifyingOfflineWorkouts))
            assertTrue(steps.contains(SyncStepState.UploadingLogs(1, 2)))
            assertTrue(steps.contains(SyncStepState.UploadingLogs(2, 2)))
            assertTrue(steps.contains(SyncStepState.Cleanup))
            assertTrue(steps.contains(SyncStepState.VerifyingActiveTraining))
            assertTrue(steps.last() is SyncStepState.Finished)

            coVerify { activeSessionRepository.clearActiveSession() }
        }
}
