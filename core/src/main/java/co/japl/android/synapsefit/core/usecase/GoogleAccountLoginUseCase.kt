package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.StateFlow

class GoogleAccountLoginUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
) {
    val authState: StateFlow<AuthState>
        get() = googleAuthRepository.authState

    suspend fun signIn(context: Any): Result<AuthState> {
        return googleAuthRepository.signIn(context)
    }

    suspend fun selectAccount(accountEmail: String): Result<AuthState> {
        return googleAuthRepository.signIn(accountEmail)
    }

    suspend fun addAnotherAccount(): Result<AuthState> {
        return googleAuthRepository.signIn("add_another_account")
    }

    suspend fun signOut(): Result<Unit> {
        return googleAuthRepository.signOut()
    }
}
