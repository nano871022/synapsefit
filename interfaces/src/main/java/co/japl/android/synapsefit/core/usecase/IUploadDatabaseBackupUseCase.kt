package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.BackupMetadata

interface IUploadDatabaseBackupUseCase {
    suspend fun execute(): Result<BackupMetadata>
}
