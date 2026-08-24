package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.DriveSyncPort
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerformDriveSyncUseCaseTest {
    private val driveSyncPort: DriveSyncPort = mockk()
    private lateinit var useCase: PerformDriveSyncUseCase

    @Before
    fun setUp() {
        useCase = PerformDriveSyncUseCase(driveSyncPort)
    }

    @Test
    fun `when databaseBytes is empty, backup returns failure`() =
        runTest {
            val result = useCase.backup(byteArrayOf())
            assertTrue(result.isFailure)
            assertEquals("Database bytes cannot be empty", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when backup succeeds, returns sync hash`() =
        runTest {
            coEvery { driveSyncPort.backupData(any()) } returns Result.success("hash123")

            val result = useCase.backup(byteArrayOf(1, 2, 3))
            assertTrue(result.isSuccess)
            assertEquals("hash123", result.getOrThrow())
        }

    @Test
    fun `when restore called, delegates to DriveSyncPort`() =
        runTest {
            val bytes = byteArrayOf(4, 5, 6)
            coEvery { driveSyncPort.restoreData() } returns Result.success(bytes)

            val result = useCase.restore()
            assertTrue(result.isSuccess)
            assertEquals(bytes, result.getOrThrow())
        }
}
