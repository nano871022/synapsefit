package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.domain.model.TableSummary

data class DatabaseSummary(
    val metadata: DatabaseMetadata,
    val tableSummaries: List<TableSummary>,
)

interface IDatabaseExplorerUseCase {
    suspend fun getDatabaseSummary(): DatabaseSummary
}
