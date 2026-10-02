package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileUseCaseTest {
    private val getUserProfileUseCase: IGetUserProfileUseCase = mockk()
    private val saveUserProfileUseCase: ISaveUserProfileUseCase = mockk()
    private val evaluateMedicalConditionsUseCase: IEvaluateMedicalConditionsUseCase = mockk()
    private val getMedicalRecommendationsUseCase: IGetMedicalRecommendationsUseCase = mockk()
    private val llmConfigRepositoryPort: LlmConfigRepositoryPort = mockk()
    private val llmClientPort: LlmClientPort = mockk()

    private val useCase = UserProfileUseCase(
        getUserProfileUseCase = getUserProfileUseCase,
        saveUserProfileUseCase = saveUserProfileUseCase,
        evaluateMedicalConditionsUseCase = evaluateMedicalConditionsUseCase,
        getMedicalRecommendationsUseCase = getMedicalRecommendationsUseCase,
        llmConfigRepositoryPort = llmConfigRepositoryPort,
        llmClientPort = llmClientPort,
    )

    @Test
    fun `checkLlmState returns MissingConfig when no active config`() = runTest {
        coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(emptyList())

        val state = useCase.checkLlmState()

        assertEquals(LlmConfigState.MissingConfig, state)
    }

    @Test
    fun `checkLlmState returns Ready when single active config succeeds`() = runTest {
        val config = LlmConfig("1", LlmProvider.GEMINI, "key1", "m1", isActive = true, 0L, 0L)
        coEvery { llmConfigRepositoryPort.getAllConfigs() } returns flowOf(listOf(config))
        coEvery { llmClientPort.testApiConnection(config) } returns Result.success(true)

        val state = useCase.checkLlmState()

        assertEquals(LlmConfigState.Ready, state)
    }
}
