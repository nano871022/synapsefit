package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort

class DatabaseExplorerUseCase(
    private val databaseManagerPort: DatabaseManagerPort,
) : IDatabaseExplorerUseCase {
    private val getDatabaseSummaryUseCase = GetDatabaseSummaryUseCase(databaseManagerPort)

    override suspend fun getDatabaseSummary(): DatabaseSummary {
        val result = getDatabaseSummaryUseCase.execute()
        val metadata = result.getOrNull() ?: DatabaseMetadata(databaseVersion = 0, fileSizeBytes = 0L, lastModifiedTimestamp = 0L, tables = emptyList())
        return DatabaseSummary(
            metadata = metadata,
            tableSummaries = metadata.tables,
        )
    }
}
