@file:Suppress("UnusedParameter")

package co.japl.android.synapsefit.app.controller.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class GoogleAuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<GoogleAuthUiState>(GoogleAuthUiState.Idle)
    val uiState: StateFlow<GoogleAuthUiState> = _uiState.asStateFlow()

    fun onGoogleLoginClicked(context: Any? = null) {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            // Secondary agent - Inject GoogleAuthRepository from :core and trigger authentication here.
            // Example stub behavior for state Flow verification:
            // val result = googleAuthRepository.signIn(context)
        }
    }

    fun onSelectAccountClicked(accountEmail: String) {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            // Secondary agent - Inject GoogleAuthRepository from :core and trigger authentication here.
            // Example:
            // val result = googleAuthRepository.selectAccount(accountEmail)
            _uiState.value = GoogleAuthUiState.Authenticated(accountEmail = accountEmail)
        }
    }

    fun onAddAnotherAccountClicked() {
        viewModelScope.launch {
            _uiState.value = GoogleAuthUiState.Authenticating
            // Secondary agent - Inject GoogleAuthRepository from :core and trigger authentication here.
            // Example:
            // googleAuthRepository.addNewAccount()
        }
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            // Secondary agent - Inject GoogleAuthRepository from :core and trigger authentication here.
            // Example:
            // googleAuthRepository.signOut()
            _uiState.value = GoogleAuthUiState.Idle
        }
    }

    fun onErrorDismissed() {
        _uiState.update { GoogleAuthUiState.Idle }
    }
}
