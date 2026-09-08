package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import kotlinx.coroutines.flow.Flow

interface LlmConfigRepositoryPort {
    fun getActiveConfig(): Flow<LlmConfig?>

    fun getAllConfigs(): Flow<List<LlmConfig>>

    suspend fun saveConfig(config: LlmConfig)

    suspend fun setActiveConfig(id: String)

    suspend fun deleteConfig(id: String)
}
