package co.japl.android.synapsefit.services.drive

import android.content.Context
import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION", "TooGenericExceptionCaught")
class GoogleAuthRepositoryImpl(
    private val context: Context? = null,
    initialState: AuthState = AuthState.Unauthenticated,
) : GoogleAuthRepository {
    private val _authState = MutableStateFlow(initialState)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        context?.let { ctx ->
            try {
                val account = GoogleSignIn.getLastSignedInAccount(ctx)
                if (account != null && !account.email.isNullOrEmpty()) {
                    val email = account.email!!
                    val displayName = account.displayName ?: email
                    _authState.value = AuthState.Authenticated(accountEmail = email, displayName = displayName)
                }
            } catch (_: Exception) {
                // Ignore startup check failure
            }
        }
    }

    override suspend fun signIn(activityContext: Any): Result<AuthState> =
        withContext(Dispatchers.IO) {
            val targetContext =
                (activityContext as? Context) ?: context
                    ?: return@withContext Result.failure(
                        IllegalStateException("Context is required for Google Sign-In"),
                    )

            try {
                val gsoBuilder =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(Scope("https://www.googleapis.com/auth/drive.appdata"))

                if (activityContext is String && activityContext.contains("@")) {
                    gsoBuilder.setAccountName(activityContext)
                }

                val gso = gsoBuilder.build()
                val client = GoogleSignIn.getClient(targetContext, gso)

                val lastAccount = GoogleSignIn.getLastSignedInAccount(targetContext)
                val account: GoogleSignInAccount =
                    if (lastAccount != null && !lastAccount.email.isNullOrEmpty()) {
                        lastAccount
                    } else {
                        val task = client.silentSignIn()
                        Tasks.await(task)
                    }

                val email = checkNotNull(account.email) { "Google account email is null" }
                val displayName = account.displayName ?: email
                val newState = AuthState.Authenticated(accountEmail = email, displayName = displayName)
                _authState.value = newState
                Result.success(newState)
            } catch (e: Exception) {
                Result.failure(e)
            } catch (e: ApiException) {
                Result.failure(e)
            }
        }

    override suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                context?.let { ctx ->
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val client = GoogleSignIn.getClient(ctx, gso)
                    Tasks.await(client.signOut())
                }
                _authState.value = AuthState.Unauthenticated
                Result.success(Unit)
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                Result.failure(e)
            }
        }

    override suspend fun getAccessToken(): Result<String> =
        withContext(Dispatchers.IO) {
            val ctx =
                context
                    ?: return@withContext Result.failure(IllegalStateException("Context is null"))

            try {
                val account =
                    GoogleSignIn.getLastSignedInAccount(ctx)
                        ?: return@withContext Result.failure(IllegalStateException("No signed in Google account found"))

                val androidAccount =
                    account.account
                        ?: return@withContext Result.failure(IllegalStateException("Google account handle is null"))

                val scope = "oauth2:https://www.googleapis.com/auth/drive.appdata"
                val token = GoogleAuthUtil.getToken(ctx, androidAccount, scope)

                if (!token.isNullOrEmpty()) {
                    val email = account.email ?: androidAccount.name
                    val tokenState = AuthState.TokenActive(accountEmail = email, accessToken = token)
                    _authState.value = tokenState
                    Result.success(token)
                } else {
                    Result.failure(IllegalStateException("Failed to retrieve access token"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
