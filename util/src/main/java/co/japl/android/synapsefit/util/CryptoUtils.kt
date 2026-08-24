package co.japl.android.synapsefit.util

import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object CryptoUtils {
    fun calculateSha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateSha256(text: String): String {
        return calculateSha256(text.toByteArray(Charsets.UTF_8))
    }

    fun calculateSha256(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
        val hashBytes = digest.digest()
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateSha256(file: File): String {
        return file.inputStream().use { calculateSha256(it) }
    }

    private const val BUFFER_SIZE = 8192
}
