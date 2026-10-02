package co.japl.android.synapsefit.app.controller.profile

import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.usecase.IUserProfileUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {
    private val useCase: IUserProfileUseCase = mockk(relaxed = true)
    private val appNavigator: AppNavigator = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { useCase.getUserProfile() } returns flowOf(null)
        coEvery { useCase.getMedicalRecommendations() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveProfile triggers LLM check when medical conditions present and halts if not Ready`() =
        runTest {
            val missingState = LlmConfigState.MissingConfig
            coEvery { useCase.checkLlmState() } returns missingState

            val viewModel =
                UserProfileViewModel(
                    userProfileUseCase = useCase,
                    appNavigator = appNavigator,
                )

            viewModel.onFullNameChange("Atleta Test")
            viewModel.onHeightCmChange("180")
            viewModel.onMedicalConditionsChange("Hipertensión")

            viewModel.saveProfile()

            assertEquals(missingState, viewModel.uiState.value.llmConfigState)
            coVerify(exactly = 0) { useCase.evaluateMedicalConditions(any(), any(), any(), any()) }
        }

    @Test
    fun `navigateToLlmSettings resets dialog and routes to settings`() =
        runTest {
            val viewModel =
                UserProfileViewModel(
                    userProfileUseCase = useCase,
                    appNavigator = appNavigator,
                )

            viewModel.navigateToLlmSettings()

            assertEquals(LlmConfigState.Ready, viewModel.uiState.value.llmConfigState)
            coVerify { appNavigator.navigateTo("settings/llm?openForm=true") }
        }
}
