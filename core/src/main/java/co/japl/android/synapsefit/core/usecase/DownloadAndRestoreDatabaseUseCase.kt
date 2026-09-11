package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import java.io.File

@Suppress("TooGenericExceptionCaught")
class DownloadAndRestoreDatabaseUseCase(
    private val driveSyncPort: DriveSyncPort,
    private val databaseManagerPort: DatabaseManagerPort,
) {
    suspend fun execute(): Result<Boolean> {
        val tempFile = File.createTempFile("drive_restore_", ".db")
        return try {
            driveSyncPort.downloadBackupFile(tempFile).getOrThrow()
            val restored = databaseManagerPort.restoreFromBackupFile(tempFile).getOrThrow()
            Result.success(restored)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
}
