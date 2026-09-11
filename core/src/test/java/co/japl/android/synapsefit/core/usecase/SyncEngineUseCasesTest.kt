package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SyncEngineUseCasesTest {
    private val driveSyncPort: DriveSyncPort = mockk()
    private val databaseManagerPort: DatabaseManagerPort = mockk()
    private val googleAuthRepository: GoogleAuthRepository = mockk()
    private val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase = mockk()

    @Test
    fun `CheckAndRestoreBackupUseCase restores when remote is newer`() =
        runTest {
            val authFlow = MutableStateFlow<AuthState>(AuthState.Authenticated("user@gmail.com"))
            coEvery { googleAuthRepository.authState } returns authFlow
            coEvery { driveSyncPort.getLastBackupMetadata() } returns
                Result.success(
                    BackupMetadata("file1", 2000L, "hash1", 1),
                )
            coEvery { databaseManagerPort.getLastLocalModifiedTimestamp() } returns 1000L
            coEvery { downloadAndRestoreDatabaseUseCase.execute() } returns Result.success(true)

            val useCase =
                CheckAndRestoreBackupUseCase(
                    googleAuthRepository,
                    driveSyncPort,
                    downloadAndRestoreDatabaseUseCase,
                    databaseManagerPort,
                )

            val result = useCase.execute()
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull() == true)
        }

    @Test
    fun `CheckAndRestoreBackupUseCase skips when local is newer or equal`() =
        runTest {
            val authFlow = MutableStateFlow<AuthState>(AuthState.Authenticated("user@gmail.com"))
            coEvery { googleAuthRepository.authState } returns authFlow
            coEvery { driveSyncPort.getLastBackupMetadata() } returns
                Result.success(
                    BackupMetadata("file1", 1000L, "hash1", 1),
                )
            coEvery { databaseManagerPort.getLastLocalModifiedTimestamp() } returns 2000L

            val useCase =
                CheckAndRestoreBackupUseCase(
                    googleAuthRepository,
                    driveSyncPort,
                    downloadAndRestoreDatabaseUseCase,
                    databaseManagerPort,
                )

            val result = useCase.execute()
            assertTrue(result.isSuccess)
            assertFalse(result.getOrNull() == true)
        }

    @Test
    fun `UploadDatabaseBackupUseCase checkpoints DB and uploads file`() =
        runTest {
            val tempFile = File.createTempFile("test_db_", ".db")
            tempFile.writeBytes("content".toByteArray())

            coEvery { databaseManagerPort.checkpoint() } returns Result.success(Unit)
            coEvery { databaseManagerPort.createBackupFile() } returns Result.success(tempFile)
            coEvery { databaseManagerPort.getDatabaseVersion() } returns 1
            coEvery { driveSyncPort.uploadBackupFile(any(), any(), any()) } returns
                Result.success(
                    BackupMetadata("id1", 3000L, "hash_abc", 1),
                )

            val useCase = UploadDatabaseBackupUseCase(databaseManagerPort, driveSyncPort)
            val result = useCase.execute()

            assertTrue(result.isSuccess)
            assertEquals("hash_abc", result.getOrNull()?.sha256Hash)
        }
}
