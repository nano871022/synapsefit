package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.DatabaseMetadata

interface IGetDatabaseSummaryUseCase {
    suspend fun execute(): Result<DatabaseMetadata>
}
