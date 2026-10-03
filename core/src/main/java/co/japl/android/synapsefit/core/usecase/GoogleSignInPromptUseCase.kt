package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.StateFlow

class GoogleSignInPromptUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
) : IGoogleSignInPromptUseCase {
    private val googleAccountLoginUseCase = GoogleAccountLoginUseCase(googleAuthRepository)

    override val authState: StateFlow<AuthState> get() = googleAccountLoginUseCase.authState
    override suspend fun signIn(context: Any) {
        googleAccountLoginUseCase.signIn(context)
    }
}
