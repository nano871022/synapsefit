package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_configs")
data class LlmConfigEntity(
    @PrimaryKey
    val id: String,
    val provider: String,
    @ColumnInfo(name = "api_key_encrypted")
    val apiKeyEncrypted: String,
    @ColumnInfo(name = "model_name")
    val modelName: String,
    @ColumnInfo(name = "is_active", defaultValue = "0")
    val isActive: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
