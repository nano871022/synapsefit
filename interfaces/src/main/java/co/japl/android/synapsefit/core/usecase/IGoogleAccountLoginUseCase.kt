package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface IGoogleAccountLoginUseCase {
    val authState: StateFlow<AuthState>

    suspend fun signIn(context: Any): Result<AuthState>

    suspend fun selectAccount(accountEmail: String): Result<AuthState>

    suspend fun addAnotherAccount(): Result<AuthState>

    suspend fun signOut(): Result<Unit>
}
