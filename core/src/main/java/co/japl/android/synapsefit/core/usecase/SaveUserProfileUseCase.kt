package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort

class SaveUserProfileUseCase(
    private val userProfileRepositoryPort: UserProfileRepositoryPort,
) {
    @Suppress("ReturnCount", "TooGenericExceptionCaught")
    suspend operator fun invoke(profile: UserProfile): Result<Unit> {
        if (profile.fullName.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Full name cannot be empty"))
        }
        if (profile.heightCm <= 0) {
            return Result.failure(IllegalArgumentException("Height must be greater than 0"))
        }

        val now = System.currentTimeMillis()
        val toSave =
            profile.copy(
                updatedAt = now,
                createdAt = if (profile.createdAt <= 0) now else profile.createdAt,
            )

        return try {
            userProfileRepositoryPort.saveUserProfile(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
