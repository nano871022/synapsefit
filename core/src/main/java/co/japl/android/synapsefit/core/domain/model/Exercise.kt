package co.japl.android.synapsefit.core.domain.model

data class Exercise(
    val id: String,
    val planId: String,
    val name: String,
    val muscleGroup: String,
    val targetSets: Int,
    val targetReps: String,
    val restSeconds: Int,
    val guideVideoUrl: String? = null,
    val guideImageUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
