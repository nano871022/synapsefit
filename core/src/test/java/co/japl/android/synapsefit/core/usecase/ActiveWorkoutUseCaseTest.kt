package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActiveWorkoutUseCaseTest {
    private lateinit var workoutPlanRepositoryPort: WorkoutPlanRepositoryPort
    private lateinit var workoutLogRepositoryPort: WorkoutLogRepositoryPort
    private lateinit var activeSessionRepositoryPort: ActiveSessionRepositoryPort
    private lateinit var wearStateMirrorPort: WearStateMirrorPort
    private lateinit var getExerciseMediaUseCase: GetExerciseMediaUseCase
    private lateinit var useCase: ActiveWorkoutUseCase

    @Before
    fun setUp() {
        workoutPlanRepositoryPort = mockk()
        workoutLogRepositoryPort = mockk(relaxed = true)
        activeSessionRepositoryPort = mockk()
        wearStateMirrorPort = mockk(relaxed = true)
        getExerciseMediaUseCase = mockk()

        useCase =
            ActiveWorkoutUseCase(
                workoutPlanRepositoryPort = workoutPlanRepositoryPort,
                workoutLogRepositoryPort = workoutLogRepositoryPort,
                activeSessionRepositoryPort = activeSessionRepositoryPort,
                wearStateMirrorPort = wearStateMirrorPort,
                getExerciseMediaUseCase = getExerciseMediaUseCase,
            )
    }

    @Test
    fun testGetPlanWithExercisesForDay() =
        runBlocking {
            val now = System.currentTimeMillis()
            val plan =
                WorkoutPlan(
                    id = "p1",
                    title = "P1",
                    goalDescription = "",
                    isActive = true,
                    generatedByLlm = false,
                    totalSessions = 12,
                    createdAt = now,
                    updatedAt = now,
                )
            val pair = Pair(plan, emptyList<co.japl.android.synapsefit.core.domain.model.Exercise>())
            every { workoutPlanRepositoryPort.getPlanWithExercisesForDay("p1", 1) } returns flowOf(pair)

            val result = useCase.getPlanWithExercisesForDay("p1", 1).first()
            assertEquals("p1", result?.first?.id)
        }

    @Test
    fun testGetActiveSessionState() =
        runBlocking {
            val state = ActiveWorkoutSessionState(planId = "p1", day = 1, isActive = true)
            every { activeSessionRepositoryPort.getActiveSessionState() } returns flowOf(state)

            val result = useCase.getActiveSessionState().first()
            assertTrue(result.isActive)
            assertEquals("p1", result.planId)
        }

    @Test
    fun testRecordWorkoutLog() =
        runBlocking {
            val result = useCase.recordWorkoutLog("p1", 1, "e1", 10, 50.0, 30L)
            assertTrue(result.isSuccess)
            coVerify { workoutLogRepositoryPort.saveLog(any()) }
        }

    @Test
    fun testResolveExerciseMedia() =
        runBlocking {
            coEvery { getExerciseMediaUseCase("e1", "Ex", "v1", "i1") } returns Pair("v2", "i2")

            val result = useCase.resolveExerciseMedia("e1", "Ex", "v1", "i1")
            assertEquals("v2", result.first)
            assertEquals("i2", result.second)
        }

    @Test
    fun testSendLiveSyncEvent() =
        runBlocking {
            val event = LiveSyncEvent.PingSession()
            useCase.sendLiveSyncEvent(event)
            coVerify { wearStateMirrorPort.sendEvent(event) }
        }
}
