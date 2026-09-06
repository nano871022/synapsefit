package co.japl.android.synapsefit.app.controller.splash

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.BodyMeasurementRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun splashViewModel_initialState_isNotReady() =
        runTest(testDispatcher) {
            val viewModel = SplashViewModel(minSplashDurationMs = 3000L)

            // Before time advances, isReady should be false
            assertFalse(viewModel.uiState.value.isReady)
            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun splashViewModel_after3Seconds_becomesReady() =
        runTest(testDispatcher) {
            val mockUserProfilePort = mockk<UserProfileRepositoryPort>()
            val mockBodyPort = mockk<BodyMeasurementRepositoryPort>()
            val mockWorkoutPort = mockk<WorkoutPlanRepositoryPort>()
            val mockLlmPort = mockk<LlmConfigRepositoryPort>()

            every { mockUserProfilePort.getUserProfile() } returns
                flowOf(
                    UserProfile(
                        id = "1",
                        fullName = "Juan",
                        birthDate = "1990-01-01",
                        gender = "MALE",
                        heightCm = 175.0,
                        bloodType = "O+",
                        createdAt = 0L,
                        updatedAt = 0L,
                    ),
                )
            every { mockBodyPort.getMeasurementsHistory() } returns flowOf(emptyList())
            every { mockWorkoutPort.getActivePlan() } returns flowOf(null)
            every { mockLlmPort.getActiveConfig() } returns flowOf(null)

            val viewModel =
                SplashViewModel(
                    userProfileRepositoryPort = mockUserProfilePort,
                    bodyMeasurementRepositoryPort = mockBodyPort,
                    workoutPlanRepositoryPort = mockWorkoutPort,
                    llmConfigRepositoryPort = mockLlmPort,
                    minSplashDurationMs = 3000L,
                )

            assertFalse(viewModel.uiState.value.isReady)

            // Advance virtual time by 3000ms
            advanceTimeBy(3000L)
            runCurrent()

            assertTrue(viewModel.uiState.value.isReady)
            assertFalse(viewModel.uiState.value.isLoading)
        }
}
