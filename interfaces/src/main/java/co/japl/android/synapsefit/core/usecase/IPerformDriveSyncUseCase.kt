package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BackupMetadata

interface IPerformDriveSyncUseCase {
    suspend fun backup(databaseBytes: ByteArray): Result<String>

    suspend fun restore(): Result<ByteArray>

    suspend fun getLastBackupMetadata(): Result<BackupMetadata?>
}
