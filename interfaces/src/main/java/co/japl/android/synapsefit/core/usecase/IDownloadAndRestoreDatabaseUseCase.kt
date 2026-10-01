package co.japl.android.synapsefit.core.usecase

interface IDownloadAndRestoreDatabaseUseCase {
    suspend fun execute(): Result<Boolean>
}
