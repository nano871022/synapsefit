package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface GoogleAuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun signIn(activityContext: Any): Result<AuthState>

    suspend fun signOut(): Result<Unit>

    suspend fun getAccessToken(): Result<String>
}
