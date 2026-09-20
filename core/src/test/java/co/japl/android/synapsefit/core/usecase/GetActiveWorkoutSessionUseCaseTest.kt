package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetActiveWorkoutSessionUseCaseTest {
    private val activeSessionRepositoryPort: ActiveSessionRepositoryPort = mockk(relaxed = true)
    private lateinit var useCase: GetActiveWorkoutSessionUseCase

    @Before
    fun setUp() {
        useCase = GetActiveWorkoutSessionUseCase(activeSessionRepositoryPort)
    }

    @Test
    fun `invoke returns active workout session state flow`() =
        runTest {
            val expectedState =
                ActiveWorkoutSessionState(
                    isActive = true,
                    activePlanDayId = "plan1_1",
                    planId = "plan1",
                    day = 1,
                    sessionStartTimestamp = 1000L,
                    activeExerciseId = "ex1",
                    exerciseStartTimestamp = 1050L,
                )

            every { activeSessionRepositoryPort.getActiveSessionState() } returns flowOf(expectedState)

            val state = useCase().first()

            assertTrue(state.isActive)
            assertEquals("plan1_1", state.activePlanDayId)
            assertEquals("plan1", state.planId)
            assertEquals(1, state.day)
            assertEquals(1000L, state.sessionStartTimestamp)
            assertEquals("ex1", state.activeExerciseId)
            assertEquals(1050L, state.exerciseStartTimestamp)
        }
}
