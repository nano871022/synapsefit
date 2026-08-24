package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.services.database.dao.LlmConfigDao
import co.japl.android.synapsefit.services.database.mapper.toDomain
import co.japl.android.synapsefit.services.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LlmConfigRepositoryAdapter(
    private val dao: LlmConfigDao,
) : LlmConfigRepositoryPort {
    override fun getActiveConfig(): Flow<LlmConfig?> =
        dao.getActiveConfig().map { entity ->
            entity?.toDomain()
        }

    override fun getAllConfigs(): Flow<List<LlmConfig>> =
        dao.getAllConfigs().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveConfig(config: LlmConfig) {
        dao.insertConfig(config.toEntity())
    }

    override suspend fun setActiveConfig(id: String) {
        dao.setActiveConfig(id)
    }

    override suspend fun deleteConfig(id: String) {
        dao.deleteConfig(id)
    }
}
