package co.japl.android.synapsefit.app.controller.history

import co.japl.android.synapsefit.core.domain.model.history.ExerciseDetailHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.usecase.GetWorkoutDetailUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutDetailViewModelTest {
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
    fun loadWorkoutDetail_populatesStateWithDetailGroup() =
        runTest {
            val useCase = mockk<GetWorkoutDetailUseCase>()
            val detailGroup =
                WorkoutDetailGroup(
                    sessionId = "plan1_1_2023-10-05",
                    planId = "plan1",
                    planTitle = "Hipertrofia Intensa",
                    day = 1,
                    dateIso = "2023-10-05",
                    timestamp = 1696500000000L,
                    exercises =
                        listOf(
                            ExerciseDetailHistory(
                                exerciseId = "ex1",
                                exerciseName = "Press de Banca Barbell",
                                exerciseDetail = "Realizar movimiento controlado",
                                muscleGroup = "Pecho",
                                sets =
                                    listOf(
                                        ExerciseSetHistory(1, 10, 80.0, 130, 60L, 1696500000000L),
                                    ),
                                averageReps = 10.0,
                                averageWeightKg = 80.0,
                            ),
                        ),
                    totalVolumeKg = 800.0,
                    totalDurationSeconds = 600L,
                )

            every { useCase("2023-10-05", 1) } returns flowOf(detailGroup)

            val viewModel = WorkoutDetailViewModel(getWorkoutDetailUseCase = useCase)
            viewModel.loadWorkoutDetail("2023-10-05", 1)

            val state = viewModel.uiState.value
            assertNotNull(state.detailGroup)
            assertEquals("Hipertrofia Intensa", state.detailGroup?.planTitle)
            assertEquals("Press de Banca Barbell", state.detailGroup?.exercises?.first()?.exerciseName)
            assertEquals("Realizar movimiento controlado", state.detailGroup?.exercises?.first()?.exerciseDetail)
        }
}
