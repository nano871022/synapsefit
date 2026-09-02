package co.japl.android.synapsefit.app.controller.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
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
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

class BackupSyncViewModel(
    private val performDriveSyncUseCase: PerformDriveSyncUseCase? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupSyncUiState())
    val uiState: StateFlow<BackupSyncUiState> = _uiState.asStateFlow()

    init {
        loadMetadata()
    }

    fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }

            if (performDriveSyncUseCase != null) {
                val metadataResult = performDriveSyncUseCase.getLastBackupMetadata()
                metadataResult.fold(
                    onSuccess = { meta ->
                        _uiState.update {
                            it.copy(
                                lastBackupTimestamp = meta?.first,
                                integrityHashSha256 = meta?.second ?: "",
                                isSyncing = false,
                                isDriveConnected = true,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                isSyncing = false,
                                errorMessage = err.message,
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    fun triggerBackup(databaseBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }

            if (performDriveSyncUseCase != null) {
                val result = performDriveSyncUseCase.backup(databaseBytes)
                result.fold(
                    onSuccess = { hash ->
                        _uiState.update {
                            it.copy(
                                integrityHashSha256 = hash,
                                lastBackupTimestamp = System.currentTimeMillis(),
                                isSyncing = false,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(
                                isSyncing = false,
                                errorMessage = err.message ?: "Error al realizar el respaldo",
                            )
                        }
                    },
                )
            } else {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }
}
