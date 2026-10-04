@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository

class SplashUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
    private val driveSyncPort: DriveSyncPort,
    private val databaseManagerPort: DatabaseManagerPort,
) : ISplashUseCase {
    private val downloadAndRestoreDatabaseUseCase = DownloadAndRestoreDatabaseUseCase(driveSyncPort, databaseManagerPort)
    private val checkAndRestoreBackupUseCase =
        CheckAndRestoreBackupUseCase(
            googleAuthRepository = googleAuthRepository,
            driveSyncPort = driveSyncPort,
            downloadAndRestoreDatabaseUseCase = downloadAndRestoreDatabaseUseCase,
            databaseManagerPort = databaseManagerPort,
        )

    override suspend fun checkAndRestoreBackup() {
        checkAndRestoreBackupUseCase.execute()
    }
}
