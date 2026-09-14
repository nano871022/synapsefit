package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort

class GetDatabaseSummaryUseCase(
    private val databaseManagerPort: DatabaseManagerPort,
) {
    suspend fun execute(): Result<DatabaseMetadata> {
        return databaseManagerPort.getDatabaseMetadata()
    }
}
