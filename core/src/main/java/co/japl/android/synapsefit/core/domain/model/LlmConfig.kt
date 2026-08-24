package co.japl.android.synapsefit.core.domain.model

data class LlmConfig(
    val id: String,
    val provider: LlmProvider,
    val apiKeyEncrypted: String,
    val modelName: String,
    val isActive: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
