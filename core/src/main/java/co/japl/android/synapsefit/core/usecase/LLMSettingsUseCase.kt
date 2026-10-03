package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import kotlinx.coroutines.flow.Flow

class LLMSettingsUseCase(
    private val llmConfigRepository: LlmConfigRepositoryPort,
) : ILLMSettingsUseCase {
    override fun getAllConfigs(): Flow<List<LlmConfig>> = llmConfigRepository.getAllConfigs()
    override suspend fun saveConfig(config: LlmConfig) {
        llmConfigRepository.saveConfig(config)
    }
    override suspend fun setActiveConfig(id: String) {
        llmConfigRepository.setActiveConfig(id)
    }
    override suspend fun deleteConfig(id: String) {
        llmConfigRepository.deleteConfig(id)
    }
}
