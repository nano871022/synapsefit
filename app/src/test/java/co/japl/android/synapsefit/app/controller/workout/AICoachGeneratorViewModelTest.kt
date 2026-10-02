package co.japl.android.synapsefit.app.controller.workout

import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.LlmErrorPayload
import co.japl.android.synapsefit.core.usecase.IAICoachGeneratorUseCase
import co.japl.android.synapsefit.navigation.AppNavigator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AICoachGeneratorViewModelTest {
    private val useCase: IAICoachGeneratorUseCase = mockk(relaxed = true)
    private val appNavigator: AppNavigator = mockk(relaxed = true)

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
    fun `checkLlmState updates state flow with result`() = runTest {
        val errorState = LlmConfigState.Error(
            LlmErrorPayload("501", "NOT_IMPLEMENTED", "Error test")
        )
        coEvery { useCase.checkLlmState() } returns errorState

        val viewModel = AICoachGeneratorViewModel(
            aiCoachGeneratorUseCase = useCase,
            appNavigator = appNavigator,
        )

        viewModel.checkLlmState()

        assertEquals(errorState, viewModel.uiState.value.llmConfigState)
    }

    @Test
    fun `generatePlan checks LLM state first and halts if not Ready`() = runTest {
        val missingState = LlmConfigState.MissingConfig
        coEvery { useCase.checkLlmState() } returns missingState

        val viewModel = AICoachGeneratorViewModel(
            aiCoachGeneratorUseCase = useCase,
            appNavigator = appNavigator,
        )

        viewModel.generatePlan()

        assertEquals(missingState, viewModel.uiState.value.llmConfigState)
        coVerify(exactly = 0) { useCase.generatePlan(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `navigateToLlmSettings resets dialog and routes to settings`() = runTest {
        val viewModel = AICoachGeneratorViewModel(
            aiCoachGeneratorUseCase = useCase,
            appNavigator = appNavigator,
        )

        viewModel.navigateToLlmSettings()

        assertEquals(LlmConfigState.Ready, viewModel.uiState.value.llmConfigState)
        coVerify { appNavigator.navigateTo("settings/llm?openForm=true") }
    }
}
