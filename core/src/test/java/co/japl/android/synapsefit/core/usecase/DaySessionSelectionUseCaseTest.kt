package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DaySessionSelectionUseCaseTest {

    private lateinit var getTodayRoutineUseCase: GetTodayRoutineUseCase
    private lateinit var activeSessionRepositoryPort: ActiveSessionRepositoryPort
    private lateinit var useCase: DaySessionSelectionUseCase

    @Before
    fun setUp() {
        getTodayRoutineUseCase = mockk()
        activeSessionRepositoryPort = mockk()
        useCase = DaySessionSelectionUseCase(
            getTodayRoutineUseCase = getTodayRoutineUseCase,
            activeSessionRepositoryPort = activeSessionRepositoryPort,
        )
    }

    @Test
    fun testGetTodayRoutines() = runBlocking {
        val item = WorkoutSessionItem(
            planId = "p1", planTitle = "Plan", day = 1, exerciseCount = 5, isTodayScheduled = true
        )
        every { getTodayRoutineUseCase.invoke() } returns flowOf(listOf(item))

        val result = useCase.getTodayRoutines().first()
        assertEquals(1, result.size)
        assertEquals("p1", result[0].planId)
    }

    @Test
    fun testGetActiveSessionState() = runBlocking {
        val state = ActiveWorkoutSessionState(planId = "p1", day = 1, isActive = true)
        every { activeSessionRepositoryPort.getActiveSessionState() } returns flowOf(state)

        val result = useCase.getActiveSessionState().first()
        assertTrue(result.isActive)
        assertEquals("p1", result.planId)
    }
}
