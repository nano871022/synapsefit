package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface IGoogleSignInPromptUseCase {
    val authState: StateFlow<AuthState>

    suspend fun signIn(context: Any)
}
