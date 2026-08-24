package co.japl.android.synapsefit.core.domain.model

data class WorkoutLog(
    val id: String,
    val exerciseId: String,
    val repsCompleted: Int,
    val weightLiftedKg: Double,
    val heartRateBpm: Int? = null,
    val sourceDevice: SourceDevice = SourceDevice.MOBILE,
    val timestamp: Long,
    val createdAt: Long,
    val updatedAt: Long
)
