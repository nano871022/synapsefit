package co.japl.android.synapsefit.services.llm

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MultiLlmClientAdapterTest {
    private val adapter = MultiLlmClientAdapter()

    @Test
    fun `generateWorkoutPlan fails if api key is blank`() =
        runTest {
            val config =
                LlmConfig(
                    id = "cfg1",
                    provider = LlmProvider.GEMINI,
                    apiKeyEncrypted = "",
                    modelName = "gemini-pro",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )

            val result =
                adapter.generateWorkoutPlan(
                    promptContext = "Build muscle",
                    environment = TrainingEnvironment.BODYWEIGHT,
                    gymChainQuery = null,
                    config = config,
                )

            assertTrue(result.isFailure)
        }

    @Test
    fun `generateWorkoutPlan returns plan and exercises for valid config`() =
        runTest {
            val config =
                LlmConfig(
                    id = "cfg1",
                    provider = LlmProvider.GEMINI,
                    apiKeyEncrypted = "valid_key",
                    modelName = "gemini-pro",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )

            val result =
                adapter.generateWorkoutPlan(
                    promptContext = "Build muscle",
                    environment = TrainingEnvironment.BODYWEIGHT,
                    gymChainQuery = null,
                    config = config,
                )

            assertTrue(result.isSuccess)
            val (plan, exercises) = result.getOrNull()!!
            assertTrue(plan.title.contains("Gemini"))
            assertEquals(2, exercises.size)
        }
}
