package co.japl.android.synapsefit.services.drive

import co.japl.android.synapsefit.util.CryptoUtils
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleDriveAppDataAdapterTest {
    private val adapter = GoogleDriveAppDataAdapter()

    @Test
    fun `backupData calculates sha256 and stores data`() =
        runTest {
            val testBytes = "sample database content".toByteArray()
            val expectedHash = CryptoUtils.calculateSha256(testBytes)

            val result = adapter.backupData(testBytes)

            assertTrue(result.isSuccess)
            assertEquals(expectedHash, result.getOrNull())

            val metadataResult = adapter.getLastBackupMetadata()
            assertTrue(metadataResult.isSuccess)
            val metadata = metadataResult.getOrNull()
            assertEquals(expectedHash, metadata?.second)
        }

    @Test
    fun `restoreData returns backed up bytes`() =
        runTest {
            val testBytes = "sample database content".toByteArray()
            adapter.backupData(testBytes)

            val restoreResult = adapter.restoreData()
            assertTrue(restoreResult.isSuccess)
            assertEquals(String(testBytes), String(restoreResult.getOrNull()!!))
        }
}
