package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata
import co.japl.android.synapsefit.core.port.secondary.DatabaseManagerPort

class GetDatabaseSummaryUseCase( : IGetDatabaseSummaryUseCase
    private val databaseManagerPort: DatabaseManagerPort,
), IGetDatabaseSummaryUseCase : IGetDatabaseSummaryUseCase {
    override override suspend override fun execute(): Result<DatabaseMetadata> {
        return databaseManagerPort.getDatabaseMetadata()
    }
}
