package co.japl.android.synapsefit.app.controller.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.domain.model.SyncState
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import co.japl.android.synapsefit.core.usecase.DownloadAndRestoreDatabaseUseCase
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
import co.japl.android.synapsefit.core.usecase.UploadDatabaseBackupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BackupSyncUiState(
    val connectedAccountEmail: String? = null,
    val isDriveConnected: Boolean = false,
    val lastBackupTimestamp: Long? = null,
    val integrityHashSha256: String = "",
    val syncState: SyncState = SyncState.Idle,
    val errorMessage: String? = null,
)

class BackupSyncViewModel(
    private val performDriveSyncUseCase: PerformDriveSyncUseCase? = null,
    private val googleAuthRepository: GoogleAuthRepository? = null,
    private val uploadDatabaseBackupUseCase: UploadDatabaseBackupUseCase? = null,
    private val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupSyncUiState())
    val uiState: StateFlow<BackupSyncUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
        loadMetadata()
    }

    private fun observeAuthState() {
        if (googleAuthRepository == null) return
        viewModelScope.launch {
            googleAuthRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        _uiState.update {
                            it.copy(
                                connectedAccountEmail = authState.accountEmail,
                                isDriveConnected = true,
                            )
                        }
                    }
                    is AuthState.TokenActive -> {
                        _uiState.update {
                            it.copy(
                                connectedAccountEmail = authState.accountEmail,
                                isDriveConnected = true,
                            )
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        _uiState.update {
                            it.copy(
                                connectedAccountEmail = null,
                                isDriveConnected = false,
                            )
                        }
                    }
                }
            }
        }
    }

    fun signIn(activityContext: Any) {
        viewModelScope.launch {
            if (googleAuthRepository == null) return@launch
            _uiState.update { it.copy(syncState = SyncState.Syncing, errorMessage = null) }
            val result = googleAuthRepository.signIn(activityContext)
            result.fold(
                onSuccess = {
                    loadMetadata()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(syncState = SyncState.Idle, errorMessage = err.message ?: "SignIn error")
                    }
                },
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            if (googleAuthRepository == null) return@launch
            googleAuthRepository.signOut()
            _uiState.update {
                it.copy(
                    connectedAccountEmail = null,
                    isDriveConnected = false,
                    lastBackupTimestamp = null,
                    integrityHashSha256 = "",
                )
            }
        }
    }

    fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncState = SyncState.Checking) }

            if (performDriveSyncUseCase != null) {
                val metadataResult = performDriveSyncUseCase.getLastBackupMetadata()
                metadataResult.fold(
                    onSuccess = { meta ->
                        _uiState.update {
                            it.copy(
                                lastBackupTimestamp = meta?.modifiedTimestamp,
                                integrityHashSha256 = meta?.sha256Hash ?: "",
                                syncState = SyncState.Idle,
                                isDriveConnected = meta != null || it.connectedAccountEmail != null,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                syncState = SyncState.Error(err.message ?: "Metadata load failed"),
                                errorMessage = err.message,
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(syncState = SyncState.Idle) }
            }
        }
    }

    fun triggerBackupNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncState = SyncState.Syncing, errorMessage = null) }

            if (uploadDatabaseBackupUseCase != null) {
                val result = uploadDatabaseBackupUseCase.execute()
                result.fold(
                    onSuccess = { meta ->
                        _uiState.update {
                            it.copy(
                                integrityHashSha256 = meta.sha256Hash,
                                lastBackupTimestamp = meta.modifiedTimestamp,
                                syncState = SyncState.Idle,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                syncState = SyncState.Error(err.message ?: "Backup failed"),
                                errorMessage = err.message ?: "Backup failed",
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(syncState = SyncState.Idle) }
            }
        }
    }

    fun triggerRestoreNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncState = SyncState.Restoring, errorMessage = null) }

            if (downloadAndRestoreDatabaseUseCase != null) {
                val result = downloadAndRestoreDatabaseUseCase.execute()
                result.fold(
                    onSuccess = {
                        loadMetadata()
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                syncState = SyncState.Error(err.message ?: "Restore failed"),
                                errorMessage = err.message ?: "Restore failed",
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(syncState = SyncState.Idle) }
            }
        }
    }

    fun triggerBackup(databaseBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(syncState = SyncState.Syncing, errorMessage = null) }

            if (performDriveSyncUseCase != null) {
                val result = performDriveSyncUseCase.backup(databaseBytes)
                result.fold(
                    onSuccess = { hash ->
                        _uiState.update {
                            it.copy(
                                integrityHashSha256 = hash,
                                lastBackupTimestamp = System.currentTimeMillis(),
                                syncState = SyncState.Idle,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                syncState = SyncState.Error(err.message ?: "Error al realizar el respaldo"),
                                errorMessage = err.message ?: "Error al realizar el respaldo",
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(syncState = SyncState.Idle) }
            }
        }
    }
}
