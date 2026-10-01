package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.StateFlow

class GoogleAccountLoginUseCase( : IGoogleAccountLoginUseCase
    private val googleAuthRepository: GoogleAuthRepository,
), IGoogleAccountLoginUseCase : IGoogleAccountLoginUseCase {
    override val authState: StateFlow<AuthState>
        get() = googleAuthRepository.authState

    override suspend override fun signIn(context: Any): Result<AuthState> {
        return googleAuthRepository.signIn(context)
    }

    override suspend override fun selectAccount(accountEmail: String): Result<AuthState> {
        return googleAuthRepository.signIn(accountEmail)
    }

    override suspend override fun addAnotherAccount(): Result<AuthState> {
        return googleAuthRepository.signIn("add_another_account")
    }

    override override suspend override fun signOut(): Result<Unit> {
        return googleAuthRepository.signOut()
    }
}
