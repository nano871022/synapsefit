package co.japl.android.synapsefit.core.domain.model

data class MedicalRecommendation(
    val id: String,
    val profileCode: String = "PRIMARY_USER",
    val result: String,
    val createdAt: Long = System.currentTimeMillis(),
)
