package co.japl.android.synapsefit.services.database

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("TooGenericExceptionCaught")
class RoomDatabaseManagerAdapter(
    private val context: Context,
    private val database: SynapseFitDatabase,
    private val databaseName: String = "synapsefit_database.db",
) : DatabaseManagerPort {
    override suspend fun checkpoint(): Result<Unit> {
        return try {
            database.openHelper.writableDatabase.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackupFile(): Result<File> {
        return try {
            checkpoint().getOrThrow()
            val dbFile = context.getDatabasePath(databaseName)
            if (!dbFile.exists()) {
                return Result.failure(IllegalStateException("Database file does not exist"))
            }
            val tempBackup = File.createTempFile("synapsefit_backup_", ".db", context.cacheDir)
            FileInputStream(dbFile).use { input ->
                FileOutputStream(tempBackup).use { output ->
                    input.copyTo(output)
                }
            }
            Result.success(tempBackup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreFromBackupFile(file: File): Result<Boolean> {
        return try {
            if (!file.exists() || file.length() == 0L) {
                return Result.failure(IllegalArgumentException("Invalid restoration file"))
            }
            database.close()
            val dbFile = context.getDatabasePath(databaseName)
            val walFile = File(dbFile.path + "-wal")
            val shmFile = File(dbFile.path + "-shm")
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            FileInputStream(file).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDatabaseVersion(): Int {
        return database.openHelper.readableDatabase.version
    }

    override fun getLastLocalModifiedTimestamp(): Long {
        val dbFile = context.getDatabasePath(databaseName)
        return if (dbFile.exists()) dbFile.lastModified() else System.currentTimeMillis()
    }
}
