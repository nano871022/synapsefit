package co.japl.android.synapsefit.core.domain.model

data class BodyMeasurement(
    val id: String,
    val weightKg: Double,
    val chestCm: Double? = null,
    val waistCm: Double? = null,
    val hipCm: Double? = null,
    val bicepLeftCm: Double? = null,
    val bicepRightCm: Double? = null,
    val thighLeftCm: Double? = null,
    val thighRightCm: Double? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
