package co.japl.android.synapsefit.core.port.secondary

import java.io.File

interface DatabaseManagerPort {
    suspend fun checkpoint(): Result<Unit>

    suspend fun createBackupFile(): Result<File>

    suspend fun restoreFromBackupFile(file: File): Result<Boolean>

    fun getDatabaseVersion(): Int

    fun getLastLocalModifiedTimestamp(): Long
}
