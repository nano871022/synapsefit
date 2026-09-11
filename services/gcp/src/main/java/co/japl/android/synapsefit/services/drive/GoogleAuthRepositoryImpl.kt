package co.japl.android.synapsefit.services.drive

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("TooGenericExceptionCaught")
class GoogleAuthRepositoryImpl(
    initialState: AuthState = AuthState.Unauthenticated,
) : GoogleAuthRepository {
    private val _authState = MutableStateFlow(initialState)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun signIn(activityContext: Any): Result<AuthState> {
        return try {
            val email = "user@gmail.com"
            val state = AuthState.Authenticated(accountEmail = email, displayName = "Google User")
            _authState.value = state
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccessToken(): Result<String> {
        return when (val current = _authState.value) {
            is AuthState.Authenticated -> Result.success("mock_access_token_appdata")
            is AuthState.TokenActive -> Result.success(current.accessToken)
            is AuthState.Unauthenticated -> Result.failure(IllegalStateException("User is not authenticated"))
        }
    }
}
