package co.japl.android.synapsefit.viewmodel

import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import co.japl.android.synapsefit.core.usecase.GetTodayRoutineUseCase
import co.japl.android.synapsefit.ui.viewmodel.WearDaySelectionViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class WearDaySelectionViewModelTest {
    private val getTodayRoutineUseCase: GetTodayRoutineUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: WearDaySelectionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateWithNullUseCase() {
        viewModel = WearDaySelectionViewModel(null)
        val state = viewModel.uiState.value
        assertTrue(state.sessions.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun testLoadSessionsSuccessEmitsData() {
        val item =
            WorkoutSessionItem(
                planId = "p1",
                planTitle = "Rutina Hipertrofia",
                day = 1,
                exerciseCount = 4,
                isTodayScheduled = true,
            )
        every { getTodayRoutineUseCase.invoke() } returns flowOf(listOf(item))

        viewModel = WearDaySelectionViewModel(getTodayRoutineUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.sessions.size)
        assertEquals("Rutina Hipertrofia", state.sessions[0].planTitle)
        assertTrue(state.sessions[0].isTodayScheduled)
        assertFalse(state.isLoading)
    }

    @Test
    fun testLoadSessionsEmptyEmitsEmptyList() {
        every { getTodayRoutineUseCase.invoke() } returns flowOf(emptyList())

        viewModel = WearDaySelectionViewModel(getTodayRoutineUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.sessions.isEmpty())
        assertFalse(state.isLoading)
    }
}
