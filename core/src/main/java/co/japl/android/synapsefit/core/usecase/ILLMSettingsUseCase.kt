package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import kotlinx.coroutines.flow.Flow

interface ILLMSettingsUseCase {
    fun getAllConfigs(): Flow<List<LlmConfig>>

    suspend fun saveConfig(config: LlmConfig)

    suspend fun setActiveConfig(id: String)

    suspend fun deleteConfig(id: String)
}
