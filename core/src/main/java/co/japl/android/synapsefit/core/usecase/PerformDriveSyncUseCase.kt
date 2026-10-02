package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort

class PerformDriveSyncUseCase(
    private val driveSyncPort: DriveSyncPort,
) : IPerformDriveSyncUseCase {
    override suspend fun backup(databaseBytes: ByteArray): Result<String> {
        if (databaseBytes.isEmpty()) {
            return Result.failure(IllegalArgumentException("Database bytes cannot be empty"))
        }
        return driveSyncPort.backupData(databaseBytes)
    }

    override suspend fun restore(): Result<ByteArray> {
        return driveSyncPort.restoreData()
    }

    override suspend fun getLastBackupMetadata(): Result<BackupMetadata?> {
        return driveSyncPort.getLastBackupMetadata()
    }
}
