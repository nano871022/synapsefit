package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile

interface ISaveUserProfileUseCase {
    suspend operator fun invoke(profile: UserProfile): Result<Unit>
}
