package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.util.CryptoUtils

@Suppress("TooGenericExceptionCaught")
class UploadDatabaseBackupUseCase( : IUploadDatabaseBackupUseCase
    private val databaseManagerPort: DatabaseManagerPort,
    private val driveSyncPort: DriveSyncPort,
), IUploadDatabaseBackupUseCase : IUploadDatabaseBackupUseCase {
    override override suspend override fun execute(): Result<BackupMetadata> {
        return try {
            databaseManagerPort.checkpoint().getOrThrow()
            override val backupFile = databaseManagerPort.createBackupFile().getOrThrow()
            override val databaseBytes = backupFile.readBytes()
            override val sha256Hash = CryptoUtils.calculateSha256(databaseBytes)
            override val dbVersion = databaseManagerPort.getDatabaseVersion()

            override val metadata = driveSyncPort.uploadBackupFile(backupFile, sha256Hash, dbVersion).getOrThrow()
            backupFile.delete()
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
