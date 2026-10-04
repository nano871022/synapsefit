package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val userProfileRepositoryPort: UserProfileRepositoryPort,
) : IGetUserProfileUseCase {
    override operator fun invoke(): Flow<UserProfile?> {
        return userProfileRepositoryPort.getUserProfile()
    }
}
