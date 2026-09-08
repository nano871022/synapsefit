package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort

class PerformDriveSyncUseCase(
    private val driveSyncPort: DriveSyncPort,
) {
    suspend fun backup(databaseBytes: ByteArray): Result<String> {
        if (databaseBytes.isEmpty()) {
            return Result.failure(IllegalArgumentException("Database bytes cannot be empty"))
        }
        return driveSyncPort.backupData(databaseBytes)
    }

    suspend fun restore(): Result<ByteArray> {
        return driveSyncPort.restoreData()
    }

    suspend fun getLastBackupMetadata(): Result<Pair<Long, String>?> {
        return driveSyncPort.getLastBackupMetadata()
    }
}
