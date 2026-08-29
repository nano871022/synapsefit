package co.japl.android.synapsefit.core.domain.model

/**
 * User Profile containing health, biometric, and medical background data.
 */
data class UserProfile(
    val id: String = "user_profile_default",
    val fullName: String,
    val birthDate: String,
    val gender: String,
    val heightCm: Double,
    val bloodType: String,
    val medicalConditions: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
