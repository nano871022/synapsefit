package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.util.CryptoUtils

@Suppress("TooGenericExceptionCaught")
class UploadDatabaseBackupUseCase(
    private val databaseManagerPort: DatabaseManagerPort,
    private val driveSyncPort: DriveSyncPort,
) {
    suspend fun execute(): Result<BackupMetadata> {
        return try {
            databaseManagerPort.checkpoint().getOrThrow()
            val backupFile = databaseManagerPort.createBackupFile().getOrThrow()
            val databaseBytes = backupFile.readBytes()
            val sha256Hash = CryptoUtils.calculateSha256(databaseBytes)
            val dbVersion = databaseManagerPort.getDatabaseVersion()

            val metadata = driveSyncPort.uploadBackupFile(backupFile, sha256Hash, dbVersion).getOrThrow()
            backupFile.delete()
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
