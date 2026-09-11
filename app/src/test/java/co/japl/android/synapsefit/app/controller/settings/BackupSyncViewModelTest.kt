package co.japl.android.synapsefit.app.controller.settings

import co.japl.android.synapsefit.core.domain.model.AuthState
import co.japl.android.synapsefit.core.domain.model.BackupMetadata
import co.japl.android.synapsefit.core.port.secondary.GoogleAuthRepository
import co.japl.android.synapsefit.core.usecase.DownloadAndRestoreDatabaseUseCase
import co.japl.android.synapsefit.core.usecase.PerformDriveSyncUseCase
import co.japl.android.synapsefit.core.usecase.UploadDatabaseBackupUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BackupSyncViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val performDriveSyncUseCase: PerformDriveSyncUseCase = mockk()
    private val googleAuthRepository: GoogleAuthRepository = mockk()
    private val uploadDatabaseBackupUseCase: UploadDatabaseBackupUseCase = mockk()
    private val downloadAndRestoreDatabaseUseCase: DownloadAndRestoreDatabaseUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn triggers Google auth and reloads metadata`() =
        runTest {
            val authFlow = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
            coEvery { googleAuthRepository.authState } returns authFlow
            coEvery { googleAuthRepository.signIn(any()) } answers {
                authFlow.value = AuthState.Authenticated("user@gmail.com")
                Result.success(AuthState.Authenticated("user@gmail.com"))
            }
            coEvery { performDriveSyncUseCase.getLastBackupMetadata() } returns
                Result.success(
                    BackupMetadata("id", 1000L, "hash123", 1),
                )

            val viewModel =
                BackupSyncViewModel(
                    performDriveSyncUseCase = performDriveSyncUseCase,
                    googleAuthRepository = googleAuthRepository,
                    uploadDatabaseBackupUseCase = uploadDatabaseBackupUseCase,
                    downloadAndRestoreDatabaseUseCase = downloadAndRestoreDatabaseUseCase,
                )

            viewModel.signIn("mockContext")
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isDriveConnected)
            assertEquals("user@gmail.com", viewModel.uiState.value.connectedAccountEmail)
            assertEquals("hash123", viewModel.uiState.value.integrityHashSha256)
        }

    @Test
    fun `triggerBackupNow updates uiState on success`() =
        runTest {
            coEvery { googleAuthRepository.authState } returns MutableStateFlow(AuthState.Unauthenticated)
            coEvery { performDriveSyncUseCase.getLastBackupMetadata() } returns Result.success(null)
            coEvery { uploadDatabaseBackupUseCase.execute() } returns
                Result.success(
                    BackupMetadata("id", 5000L, "hash_backed_up", 1),
                )

            val viewModel =
                BackupSyncViewModel(
                    performDriveSyncUseCase = performDriveSyncUseCase,
                    googleAuthRepository = googleAuthRepository,
                    uploadDatabaseBackupUseCase = uploadDatabaseBackupUseCase,
                    downloadAndRestoreDatabaseUseCase = downloadAndRestoreDatabaseUseCase,
                )

            viewModel.triggerBackupNow()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("hash_backed_up", viewModel.uiState.value.integrityHashSha256)
            assertEquals(5000L, viewModel.uiState.value.lastBackupTimestamp)
        }
}
