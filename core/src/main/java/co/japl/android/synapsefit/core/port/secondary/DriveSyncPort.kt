package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import java.io.File

interface DriveSyncPort {
    suspend fun backupData(databaseBytes: ByteArray): Result<String>

    suspend fun restoreData(): Result<ByteArray>

    suspend fun getLastBackupMetadata(): Result<BackupMetadata?>

    suspend fun uploadBackupFile(
        file: File,
        sha256Hash: String,
        dbVersion: Int,
    ): Result<BackupMetadata>

    suspend fun downloadBackupFile(targetFile: File): Result<File>
}
