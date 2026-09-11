package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository

@Suppress("TooGenericExceptionCaught", "ReturnCount")
class CheckAndRestoreBackupUseCase(
    private val googleAuthRepository: GoogleAuthRepository,
    private val driveSyncPort: DriveSyncPort,
    private val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase,
    private val databaseManagerPort: DatabaseManagerPort,
) {
    suspend fun execute(): Result<Boolean> {
        return try {
            val authState = googleAuthRepository.authState.value
            if (authState !is AuthState.Authenticated && authState !is AuthState.TokenActive) {
                return Result.success(false)
            }

            val remoteMetadataResult = driveSyncPort.getLastBackupMetadata()
            val remoteMetadata = remoteMetadataResult.getOrNull() ?: return Result.success(false)

            val localTimestamp = databaseManagerPort.getLastLocalModifiedTimestamp()
            if (remoteMetadata.modifiedTimestamp > localTimestamp) {
                val restoreResult = downloadAndRestoreDatabaseUseCase.execute()
                restoreResult
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
