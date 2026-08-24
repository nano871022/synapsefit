package co.japl.android.synapsefit.services.drive

import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.util.CryptoUtils

@Suppress("TooGenericExceptionCaught")
class GoogleDriveAppDataAdapter : DriveSyncPort {
    private var remoteBackupData: ByteArray? = null
    private var lastBackupTimestamp: Long? = null
    private var lastBackupHash: String? = null

    override suspend fun backupData(databaseBytes: ByteArray): Result<String> {
        return try {
            val hash = CryptoUtils.calculateSha256(databaseBytes)
            remoteBackupData = databaseBytes
            lastBackupTimestamp = System.currentTimeMillis()
            lastBackupHash = hash
            Result.success(hash)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreData(): Result<ByteArray> {
        return try {
            val data = remoteBackupData
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(IllegalStateException("No remote backup found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastBackupMetadata(): Result<Pair<Long, String>?> {
        return try {
            val timestamp = lastBackupTimestamp
            val hash = lastBackupHash
            if (timestamp != null && hash != null) {
                Result.success(Pair(timestamp, hash))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
