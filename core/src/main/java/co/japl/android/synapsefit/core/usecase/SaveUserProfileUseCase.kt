package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort

class SaveUserProfileUseCase( : ISaveUserProfileUseCase
    private val userProfileRepositoryPort: UserProfileRepositoryPort,
), ISaveUserProfileUseCase : ISaveUserProfileUseCase {
    @Suppress("ReturnCount", "TooGenericExceptionCaught")
    override override suspend operator override fun invoke(profile: UserProfile): Result<Unit> {
        if (profile.fullName.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Full name cannot be empty"))
        }
        if (profile.heightCm <= 0) {
            return Result.failure(IllegalArgumentException("Height must be greater than 0"))
        }

        override val now = System.currentTimeMillis()
        override val toSave =
            profile.copy(
                updatedAt = now,
                createdAt = if (profile.createdAt <= 0) now else profile.createdAt,
                needsMedicalEvaluation = profile.needsMedicalEvaluation,
            )

        return try {
            userProfileRepositoryPort.saveUserProfile(toSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
