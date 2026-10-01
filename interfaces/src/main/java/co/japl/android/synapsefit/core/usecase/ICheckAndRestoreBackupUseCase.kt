package co.japl.android.synapsefit.core.usecase

interface ICheckAndRestoreBackupUseCase {
    suspend fun execute(): Result<Boolean>
}
