package co.japl.android.synapsefit.services.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.japl.android.synapsefit.services.database.entity.LlmConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmConfigDao {
    @Query("SELECT * FROM llm_configs WHERE is_active = 1 ORDER BY updated_at DESC, created_at DESC LIMIT 1")
    fun getActiveConfig(): Flow<LlmConfigEntity?>

    @Query("SELECT * FROM llm_configs ORDER BY updated_at DESC")
    fun getAllConfigs(): Flow<List<LlmConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: LlmConfigEntity)

    @Query("UPDATE llm_configs SET is_active = 0 WHERE id != :activeId")
    suspend fun deactivateOtherConfigs(activeId: String)

    @Query("UPDATE llm_configs SET is_active = 1 WHERE id = :id")
    suspend fun activateConfig(id: String)

    @Transaction
    suspend fun setActiveConfig(id: String) {
        deactivateOtherConfigs(id)
        activateConfig(id)
    }

    @Query("DELETE FROM llm_configs WHERE id = :id")
    suspend fun deleteConfig(id: String)
}
