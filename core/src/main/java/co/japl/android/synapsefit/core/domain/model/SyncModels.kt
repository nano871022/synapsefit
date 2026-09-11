package co.japl.android.synapsefit.core.domain.model

sealed class AuthState {
    object Unauthenticated : AuthState()

    data class Authenticated(val accountEmail: String, val displayName: String? = null) : AuthState()

    data class TokenActive(val accountEmail: String, val accessToken: String) : AuthState()
}

sealed class SyncState {
    object Idle : SyncState()

    object Checking : SyncState()

    object Restoring : SyncState()

    object Syncing : SyncState()

    data class Error(val message: String) : SyncState()
}

data class BackupMetadata(
    val fileId: String,
    val modifiedTimestamp: Long,
    val sha256Hash: String,
    val dbVersion: Int = 1,
)
