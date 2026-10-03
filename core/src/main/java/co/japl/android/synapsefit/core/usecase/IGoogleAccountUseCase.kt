package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.domain.model.SyncState
import kotlinx.coroutines.flow.StateFlow

interface IGoogleAccountUseCase {
    val authState: StateFlow<AuthState>
    val syncState: StateFlow<SyncState>
    val lastBackupMetadata: StateFlow<BackupMetadata?>
    suspend fun signIn(context: Any)
    suspend fun signOut()
    suspend fun uploadBackup(): Boolean
    suspend fun downloadAndRestoreBackup(): Boolean
}
