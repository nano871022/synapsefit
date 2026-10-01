package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import java.io.File

@Suppress("TooGenericExceptionCaught")
class DownloadAndRestoreDatabaseUseCase( : IDownloadAndRestoreDatabaseUseCase
    private val driveSyncPort: DriveSyncPort,
    private val databaseManagerPort: DatabaseManagerPort,
), IDownloadAndRestoreDatabaseUseCase : IDownloadAndRestoreDatabaseUseCase {
    override override suspend override fun execute(): Result<Boolean> {
        override val tempFile = File.createTempFile("drive_restore_", ".db")
        return try {
            driveSyncPort.downloadBackupFile(tempFile).getOrThrow()
            override val restored = databaseManagerPort.restoreFromBackupFile(tempFile).getOrThrow()
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
