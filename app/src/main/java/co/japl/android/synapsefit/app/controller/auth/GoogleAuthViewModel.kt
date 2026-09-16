package co.japl.android.synapsefit.app.controller.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GoogleAuthUiState {
    object Idle : GoogleAuthUiState

    object Authenticating : GoogleAuthUiState

    data class Authenticated(
        val accountEmail: String,
        val displayName: String? = null,
        val photoUrl: String? = null,
        val availableAccounts: List<String> = emptyList(),
    ) : GoogleAuthUiState

    data class AccountSelectionRequired(
        val availableAccounts: List<String>,
    ) : GoogleAuthUiState

    data class Error(
        val message: String,
    ) : GoogleAuthUiState
}

class GoogleAuthViewModel(
    private val googleAuthRepository: GoogleAuthRepository? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow<GoogleAuthUiState>(GoogleAuthUiState.Idle)
    val uiState: StateFlow<GoogleAuthUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        if (googleAuthRepository == null) return
        viewModelScope.launch {
            googleAuthRepository.authState.collect { authState ->
                handleAuthStateUpdate(authState)
            }
        }
    }

    fun onGoogleLoginClicked(context: Any? = null) {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            if (googleAuthRepository != null) {
                val result = googleAuthRepository.signIn(context ?: Any())
                result.fold(
                    onSuccess = { authState ->
                        handleAuthStateUpdate(authState)
                    },
                    onFailure = { err ->
                        _uiState.value =
                            GoogleAuthUiState.Error(
                                message = err.message ?: "Authentication failed",
                            )
                    },
                )
            }
        }
    }

    fun onSelectAccountClicked(accountEmail: String) {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            if (googleAuthRepository != null) {
                val result = googleAuthRepository.signIn(accountEmail)
                result.fold(
                    onSuccess = { authState ->
                        handleAuthStateUpdate(authState)
                    },
                    onFailure = { err ->
                        _uiState.value =
                            GoogleAuthUiState.Error(
                                message = err.message ?: "Account selection failed",
                            )
                    },
                )
            } else {
                _uiState.value = GoogleAuthUiState.Authenticated(accountEmail = accountEmail)
            }
        }
    }

    fun onAddAnotherAccountClicked() {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            if (googleAuthRepository != null) {
                val result = googleAuthRepository.signIn("add_another_account")
                result.fold(
                    onSuccess = { authState ->
                        handleAuthStateUpdate(authState)
                    },
                    onFailure = { err ->
                        _uiState.value =
                            GoogleAuthUiState.Error(
                                message = err.message ?: "Failed to add account",
                            )
                    },
                )
            }
        }
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            if (googleAuthRepository != null) {
                val result = googleAuthRepository.signOut()
                result.fold(
                    onSuccess = {
                        _uiState.value = GoogleAuthUiState.Idle
                    },
                    onFailure = { err ->
                        _uiState.value =
                            GoogleAuthUiState.Error(
                                message = err.message ?: "Sign out failed",
                            )
                    },
                )
            } else {
                _uiState.value = GoogleAuthUiState.Idle
            }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { GoogleAuthUiState.Idle }
    }

    private fun handleAuthStateUpdate(authState: AuthState) {
        when (authState) {
            is AuthState.Authenticated -> {
                _uiState.value =
                    GoogleAuthUiState.Authenticated(
                        accountEmail = authState.accountEmail,
                        displayName = authState.displayName,
                    )
            }
            is AuthState.TokenActive -> {
                _uiState.value =
                    GoogleAuthUiState.Authenticated(
                        accountEmail = authState.accountEmail,
                    )
            }
            is AuthState.Unauthenticated -> {
                _uiState.value = GoogleAuthUiState.Idle
            }
        }
    }
}
