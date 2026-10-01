package co.japl.android.synapsefit.core.domain.model

data class WorkoutPlan(
    val id: String,
    val title: String,
    val goalDescription: String,
    val isActive: Boolean = true,
    val generatedByLlm: Boolean = false,
    val totalSessions: Int = 12,
    val createdAt: Long,
    val updatedAt: Long,
)
