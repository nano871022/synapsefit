package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AICoachGeneratorUseCaseTest {

    private val llmConfigRepositoryPort: LlmConfigRepositoryPort = mockk()
    private val llmClientPort: LlmClientPort = mockk()
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort = mockk()
    private val userProfileRepositoryPort: UserProfileRepositoryPort = mockk()

    private lateinit var useCase: AICoachGeneratorUseCase

    @Before
    fun setUp() {
        useCase = AICoachGeneratorUseCase(
            llmConfigRepository = llmConfigRepositoryPort,
            llmClient = llmClientPort,
            workoutPlanRepository = workoutPlanRepositoryPort,
            userProfileRepository = userProfileRepositoryPort,
        )
    }

    @Test
    fun `generatePlan fails when no active LLM config`() = runTest {
        every { llmConfigRepositoryPort.getActiveConfig() } returns flowOf(null)

        val result = useCase.generatePlan("Build muscle")

        assertTrue(result.isFailure)
    }

    @Test
    fun `optimizePrompt returns success when LLM config active`() = runTest {
        val now = System.currentTimeMillis()
        val config = LlmConfig(
            id = "1",
            provider = LlmProvider.GEMINI,
            modelName = "gemini-pro",
            apiKeyEncrypted = "key",
            isActive = true,
            createdAt = now,
            updatedAt = now,
        )
        every { llmConfigRepositoryPort.getActiveConfig() } returns flowOf(config)
        coEvery { llmClientPort.testApiConnection(config) } returns Result.success(true)
        coEvery { llmClientPort.optimizePrompt(any(), any(), any(), any()) } returns Result.success("Optimized Prompt")

        val result = useCase.optimizePrompt("Build muscle")

        assertTrue(result.isSuccess)
        assertEquals("Optimized Prompt", result.getOrNull())
    }
}
