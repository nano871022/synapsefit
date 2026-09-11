package co.japl.android.synapsefit.services.drive

import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.util.CryptoUtils
import java.io.File

@Suppress("TooGenericExceptionCaught")
class GoogleDriveAppDataAdapter : DriveSyncPort {
    private var remoteBackupData: ByteArray? = null
    private var lastMetadata: BackupMetadata? = null

    override suspend fun backupData(databaseBytes: ByteArray): Result<String> {
        return try {
            val hash = CryptoUtils.calculateSha256(databaseBytes)
            remoteBackupData = databaseBytes
            val metadata =
                BackupMetadata(
                    fileId = "appDataFolder_file_1",
                    modifiedTimestamp = System.currentTimeMillis(),
                    sha256Hash = hash,
                    dbVersion = 1,
                )
            lastMetadata = metadata
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

    override suspend fun getLastBackupMetadata(): Result<BackupMetadata?> {
        return try {
            Result.success(lastMetadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadBackupFile(
        file: File,
        sha256Hash: String,
        dbVersion: Int,
    ): Result<BackupMetadata> {
        return try {
            val bytes = file.readBytes()
            remoteBackupData = bytes
            val metadata =
                BackupMetadata(
                    fileId = "drive_appdata_" + System.currentTimeMillis(),
                    modifiedTimestamp = System.currentTimeMillis(),
                    sha256Hash = sha256Hash,
                    dbVersion = dbVersion,
                )
            lastMetadata = metadata
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadBackupFile(targetFile: File): Result<File> {
        return try {
            val bytes = remoteBackupData ?: return Result.failure(IllegalStateException("No backup found on Drive"))
            targetFile.writeBytes(bytes)
            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
