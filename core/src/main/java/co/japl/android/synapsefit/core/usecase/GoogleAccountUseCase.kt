package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.domain.model.SyncState
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoogleAccountUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
    private val driveSyncPort: DriveSyncPort,
    private val databaseManagerPort: DatabaseManagerPort,
) : IGoogleAccountUseCase {
    private val googleAccountLoginUseCase = GoogleAccountLoginUseCase(googleAuthRepository)
    private val uploadDatabaseBackupUseCase = UploadDatabaseBackupUseCase(databaseManagerPort, driveSyncPort)
    private val downloadAndRestoreDatabaseUseCase = DownloadAndRestoreDatabaseUseCase(driveSyncPort, databaseManagerPort)

    override val authState: StateFlow<AuthState> get() = googleAuthRepository.authState
    override val syncState: StateFlow<SyncState> get() = MutableStateFlow(SyncState.Idle)
    override val lastBackupMetadata: StateFlow<BackupMetadata?> get() = MutableStateFlow(null)

    override suspend fun signIn(context: Any) {
        googleAccountLoginUseCase.signIn(context)
    }
    override suspend fun signOut() {
        googleAccountLoginUseCase.signOut()
    }
    override suspend fun uploadBackup(): Boolean = uploadDatabaseBackupUseCase.execute().isSuccess
    override suspend fun downloadAndRestoreBackup(): Boolean = downloadAndRestoreDatabaseUseCase.execute().isSuccess
}
