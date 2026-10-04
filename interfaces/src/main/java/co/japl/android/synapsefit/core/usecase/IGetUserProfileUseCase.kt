package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IGetUserProfileUseCase {
    operator fun invoke(): Flow<UserProfile?>
}
