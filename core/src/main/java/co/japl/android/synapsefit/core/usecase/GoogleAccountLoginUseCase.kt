package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.StateFlow

class GoogleAccountLoginUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
) : IGoogleAccountLoginUseCase {
    override val authState: StateFlow<AuthState>
        get() = googleAuthRepository.authState

    override suspend fun signIn(context: Any): Result<AuthState> {
        return googleAuthRepository.signIn(context)
    }

    override suspend fun selectAccount(accountEmail: String): Result<AuthState> {
        return googleAuthRepository.signIn(accountEmail)
    }

    override suspend fun addAnotherAccount(): Result<AuthState> {
        return googleAuthRepository.signIn("add_another_account")
    }

    override suspend fun signOut(): Result<Unit> {
        return googleAuthRepository.signOut()
    }
}
