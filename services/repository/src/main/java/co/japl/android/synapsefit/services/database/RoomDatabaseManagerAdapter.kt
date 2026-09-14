package co.japl.android.synapsefit.services.database

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.domain.model.TableSummary
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("TooGenericExceptionCaught", "MagicNumber", "NestedBlockDepth")
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

    override suspend fun getTableSummaries(): Result<List<TableSummary>> {
        return try {
            val db = database.openHelper.readableDatabase
            val knownTables =
                listOf(
                    "user_profile",
                    "tbl_medical_result",
                    "workout_plans",
                    "exercises",
                    "workout_logs",
                    "body_measurements",
                    "llm_configs",
                )
            val summaries =
                knownTables.map { tableName ->
                    buildTableSummary(db, tableName)
                }
            Result.success(summaries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildTableSummary(
        db: SupportSQLiteDatabase,
        tableName: String,
    ): TableSummary {
        var count = 0L
        val countCursor = db.query(SimpleSQLiteQuery("SELECT COUNT(*) FROM `$tableName`"))
        if (countCursor.moveToFirst()) {
            count = countCursor.getLong(0)
        }
        countCursor.close()

        val columns = mutableListOf<String>()
        val pragmaCursor = db.query(SimpleSQLiteQuery("PRAGMA table_info(`$tableName`)"))
        while (pragmaCursor.moveToNext()) {
            val nameIndex = pragmaCursor.getColumnIndex("name")
            if (nameIndex >= 0) {
                columns.add(pragmaCursor.getString(nameIndex))
            }
        }
        pragmaCursor.close()

        return TableSummary(
            tableName = tableName,
            recordCount = count,
            columnCount = columns.size,
            columns = columns,
        )
    }

    override suspend fun getDatabaseMetadata(): Result<DatabaseMetadata> {
        return try {
            val dbFile = context.getDatabasePath(databaseName)
            val fileSize = if (dbFile.exists()) dbFile.length() else 0L
            val lastModified = if (dbFile.exists()) dbFile.lastModified() else System.currentTimeMillis()
            val version = getDatabaseVersion()
            val tables = getTableSummaries().getOrDefault(emptyList())

            Result.success(
                DatabaseMetadata(
                    databaseVersion = version,
                    fileSizeBytes = fileSize,
                    lastModifiedTimestamp = lastModified,
                    tables = tables,
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
