@file:Suppress("FunctionNaming", "LongMethod")

package co.japl.android.synapsefit.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing
import co.japl.android.synapsefit.util.DateTimeUtils

@Composable
fun BackupSyncScreen(
    state: BackupSyncUiState,
    onBackupNowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.backup_sync),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        state.errorMessage?.let { err ->
            Text(
                text = err,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        GoogleDriveAccountCard(
            connectedEmail = state.connectedAccountEmail,
            isConnected = state.isDriveConnected,
        )

        BackupMetadataCard(
            lastBackupTimestamp = state.lastBackupTimestamp,
            sha256Hash = state.integrityHashSha256,
        )

        NeonButton(
            text = stringResource(R.string.backup_now),
            onClick = onBackupNowClick,
            isLoading = state.isSyncing,
        )
    }
}

@Composable
fun GoogleDriveAccountCard(
    connectedEmail: String?,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    KineticCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.drive_appdata),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                val statusText =
                    connectedEmail ?: if (isConnected) {
                        stringResource(R.string.connected)
                    } else {
                        stringResource(R.string.not_connected)
                    }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = if (isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null,
                tint = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun BackupMetadataCard(
    lastBackupTimestamp: Long?,
    sha256Hash: String,
    modifier: Modifier = Modifier,
) {
    KineticCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.last_backup_details),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            val dateText =
                if (lastBackupTimestamp != null) {
                    DateTimeUtils.formatEpoch(lastBackupTimestamp)
                } else {
                    stringResource(R.string.no_backups)
                }
            Text(
                text = stringResource(R.string.date_prefix, dateText),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (sha256Hash.isNotBlank()) {
                Text(
                    text = stringResource(R.string.sha256_hash),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = sha256Hash,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
