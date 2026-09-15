@file:Suppress("LongParameterList", "FunctionNaming", "LongMethod", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.auth.GoogleAuthUiState
import co.japl.android.synapsefit.app.controller.settings.BackupSyncUiState
import co.japl.android.synapsefit.app.ui.auth.GoogleAccountSelectionDialog
import co.japl.android.synapsefit.app.ui.auth.GoogleAuthStatusCard
import co.japl.android.synapsefit.app.ui.auth.GoogleSignInPromptScreen
import co.japl.android.synapsefit.core.domain.model.SyncState
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.util.DateTimeUtils

@Suppress("LongParameterList")
@Composable
fun BackupSyncScreen(
    googleAuthState: GoogleAuthUiState,
    syncState: BackupSyncUiState,
    onGoogleLoginClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onSelectAccountClick: (String) -> Unit,
    onAddAnotherAccountClick: () -> Unit,
    onDismissAccountSelection: () -> Unit,
    onBackupNowClick: () -> Unit,
    onRestoreNowClick: () -> Unit,
    onNavigateDbExplorer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.marginEdge)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.google_auth_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Section 1: Google Account Authentication Card / Login Prompt
        when (googleAuthState) {
            is GoogleAuthUiState.Authenticated -> {
                GoogleAuthStatusCard(
                    accountEmail = googleAuthState.accountEmail,
                    displayName = googleAuthState.displayName,
                    onSignOutClick = onSignOutClick,
                    onChangeAccountClick =
                        if (googleAuthState.availableAccounts.size > 1) {
                            { onSelectAccountClick(googleAuthState.accountEmail) }
                        } else {
                            null
                        },
                )
            }
            else -> {
                GoogleSignInPromptScreen(
                    state = googleAuthState,
                    onGoogleLoginClick = onGoogleLoginClick,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        // Section 2: Backup and Restore Controls
        Text(
            text = stringResource(R.string.backup_sync),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        syncState.errorMessage?.let { err ->
            Text(
                text = err,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        BackupMetadataCard(
            lastBackupTimestamp = syncState.lastBackupTimestamp,
            sha256Hash = syncState.integrityHashSha256,
        )

        val isLoading =
            syncState.syncState is SyncState.Syncing ||
                syncState.syncState is SyncState.Restoring ||
                syncState.syncState is SyncState.Checking

        val isConnected = googleAuthState is GoogleAuthUiState.Authenticated || syncState.isDriveConnected

        NeonButton(
            text = stringResource(R.string.backup_now),
            onClick = onBackupNowClick,
            isLoading = isLoading,
        )

        OutlinedButton(
            onClick = onRestoreNowClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isConnected,
        ) {
            Text(text = stringResource(R.string.restore_from_drive))
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        // Section 3: Database Explorer Shortcut
        Text(
            text = stringResource(R.string.tools_and_storage),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        NavigationShortcutCard(
            title = stringResource(R.string.db_explorer_title),
            description = stringResource(R.string.db_explorer_desc),
            icon = Icons.Default.Storage,
            onClick = onNavigateDbExplorer,
        )
    }

    if (googleAuthState is GoogleAuthUiState.AccountSelectionRequired) {
        GoogleAccountSelectionDialog(
            availableAccounts = googleAuthState.availableAccounts,
            onAccountSelected = onSelectAccountClick,
            onAddAccountClick = onAddAnotherAccountClick,
            onDismissRequest = onDismissAccountSelection,
        )
    }
}

@Composable
private fun NavigationShortcutCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            KineticCard(
                modifier = Modifier,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun GoogleDriveAccountCard(
    connectedEmail: String?,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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

            if (isConnected) {
                OutlinedButton(onClick = onSignOutClick, modifier = Modifier.align(Alignment.End)) {
                    Text(text = stringResource(R.string.sign_out))
                }
            } else {
                OutlinedButton(onClick = onSignInClick, modifier = Modifier.align(Alignment.End)) {
                    Text(text = stringResource(R.string.sign_in_google))
                }
            }
        }
    }
}

@Composable
fun BackupMetadataCard(
    lastBackupTimestamp: Long?,
    sha256Hash: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.last_backup_details).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val dateText =
                if (lastBackupTimestamp != null) {
                    DateTimeUtils.formatEpoch(lastBackupTimestamp)
                } else {
                    stringResource(R.string.no_backups)
                }
            Text(
                text = stringResource(R.string.date_prefix, dateText),
                style = MaterialTheme.typography.titleMedium,
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

@Preview(showBackground = true)
@Composable
fun BackupSyncScreenPreview() {
    MaterialThemeComposeUI {
        BackupSyncScreen(
            googleAuthState = GoogleAuthUiState.Authenticated("usuario@gmail.com", "Atleta Synapse"),
            syncState =
                BackupSyncUiState(
                    connectedAccountEmail = "usuario@gmail.com",
                    isDriveConnected = true,
                    lastBackupTimestamp = System.currentTimeMillis(),
                    integrityHashSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                ),
            onGoogleLoginClick = {},
            onSignOutClick = {},
            onSelectAccountClick = {},
            onAddAnotherAccountClick = {},
            onDismissAccountSelection = {},
            onBackupNowClick = {},
            onRestoreNowClick = {},
            onNavigateDbExplorer = {},
        )
    }
}
