package co.japl.android.synapsefit.core.usecase

import app.cash.turbine.test
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LlmConfigFlowTest {
    private val repositoryPort: LlmConfigRepositoryPort = mockk()

    @Test
    fun `when observing active config flow emissions are received properly`() =
        runTest {
            val config =
                LlmConfig(
                    id = "cfg-1",
                    provider = LlmProvider.GEMINI,
                    apiKeyEncrypted = "encrypted_key",
                    modelName = "gemini-1.5-pro",
                    isActive = true,
                    createdAt = 1000L,
                    updatedAt = 1000L,
                )

            every { repositoryPort.getActiveConfig() } returns flowOf(config)

            repositoryPort.getActiveConfig().test {
                val item = awaitItem()
                assertNotNull(item)
                assertEquals("cfg-1", item?.id)
                assertEquals(LlmProvider.GEMINI, item?.provider)
                assertEquals("gemini-1.5-pro", item?.modelName)
                awaitComplete()
            }
        }
}
