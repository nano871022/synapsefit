package co.japl.android.synapsefit.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

class CryptoUtilsTest {
    @Test
    fun testCalculateSha256ForByteArray() {
        val input = "SynapseFit".toByteArray(Charsets.UTF_8)
        val hash = CryptoUtils.calculateSha256(input)
        assertEquals(64, hash.length)
        assertEquals(hash, CryptoUtils.calculateSha256("SynapseFit"))
    }

    @Test
    fun testCalculateSha256ForString() {
        val hash1 = CryptoUtils.calculateSha256("Hello World")
        val hash2 = CryptoUtils.calculateSha256("Hello World")
        val hash3 = CryptoUtils.calculateSha256("hello world")

        assertEquals(64, hash1.length)
        assertEquals(hash1, hash2)
        assertNotEquals(hash1, hash3)
    }

    @Test
    fun testCalculateSha256ForInputStream() {
        val data = "Test Stream Content"
        val inputStream = ByteArrayInputStream(data.toByteArray(Charsets.UTF_8))
        val hashFromStream = CryptoUtils.calculateSha256(inputStream)
        val hashFromString = CryptoUtils.calculateSha256(data)

        assertEquals(hashFromString, hashFromStream)
    }

    @Test
    fun testCalculateSha256ForFile() {
        val tempFile = File.createTempFile("synapsefit_test", ".tmp")
        try {
            tempFile.writeText("File content for hash test", Charsets.UTF_8)
            val fileHash = CryptoUtils.calculateSha256(tempFile)
            val stringHash = CryptoUtils.calculateSha256("File content for hash test")

            assertEquals(stringHash, fileHash)
        } finally {
            tempFile.delete()
        }
    }
}
