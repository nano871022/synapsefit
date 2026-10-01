package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.domain.model.TableSummary
import java.io.File

interface DatabaseManagerPort {
    suspend fun checkpoint(): Result<Unit>

    suspend fun createBackupFile(): Result<File>

    suspend fun restoreFromBackupFile(file: File): Result<Boolean>

    fun getDatabaseVersion(): Int

    fun getLastLocalModifiedTimestamp(): Long

    suspend fun getTableSummaries(): Result<List<TableSummary>>

    suspend fun getDatabaseMetadata(): Result<DatabaseMetadata>
}
