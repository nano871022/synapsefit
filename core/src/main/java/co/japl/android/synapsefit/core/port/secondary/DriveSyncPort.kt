package co.japl.android.synapsefit.core.port.secondary

interface DriveSyncPort {
    suspend fun backupData(databaseBytes: ByteArray): Result<String>
    suspend fun restoreData(): Result<ByteArray>
    suspend fun getLastBackupMetadata(): Result<Pair<Long, String>?>
}
