@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList", "MaxLineLength")

package co.japl.android.synapsefit.app.ui.profile

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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import co.japl.android.synapsefit.app.controller.profile.UserProfileUiState
import co.japl.android.synapsefit.app.ui.auth.GoogleAccountSelectionDialog
import co.japl.android.synapsefit.app.ui.auth.GoogleAuthStatusCard
import co.japl.android.synapsefit.app.ui.auth.GoogleSignInPromptScreen
import co.japl.android.synapsefit.ui.components.KineticCard

@Composable
fun ProfileConnectionScreen(
    googleAuthState: GoogleAuthUiState,
    profileState: UserProfileUiState,
    onGoogleLoginClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onSelectAccountClick: (String) -> Unit,
    onAddAnotherAccountClick: () -> Unit,
    onDismissAccountSelection: () -> Unit,
    onNavigateBackup: () -> Unit,
    onNavigateDbExplorer: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onHeightCmChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onMedicalConditionsChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRecalculateMedicalEvaluation: () -> Unit = {},
    onRetryMedicalConditions: () -> Unit = {},
    onDismissMedicalDialog: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.marginEdge)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.profile_and_connection_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // Section 1: Google Account Authentication Status / Actions
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

        // Section 2: Navigation Shortcuts Cards to "Respaldo y Nube" & "Explorador de Base de Datos"
        Text(
            text = stringResource(R.string.tools_and_storage),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp),
        )

        NavigationShortcutCard(
            title = stringResource(R.string.backup_sync),
            description = stringResource(R.string.backup_sync_desc),
            icon = Icons.Default.CloudUpload,
            onClick = onNavigateBackup,
        )

        NavigationShortcutCard(
            title = stringResource(R.string.db_explorer_title),
            description = stringResource(R.string.db_explorer_desc),
            icon = Icons.Default.Storage,
            onClick = onNavigateDbExplorer,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // Section 3: Detailed User Profile Section
        Text(
            text = stringResource(R.string.user_profile),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        UserProfileScreen(
            state = profileState,
            onFullNameChange = onFullNameChange,
            onBirthDateChange = onBirthDateChange,
            onGenderChange = onGenderChange,
            onHeightCmChange = onHeightCmChange,
            onBloodTypeChange = onBloodTypeChange,
            onMedicalConditionsChange = onMedicalConditionsChange,
            onSaveClick = onSaveProfileClick,
            onRecalculateMedicalEvaluation = onRecalculateMedicalEvaluation,
            onRetryMedicalConditions = onRetryMedicalConditions,
            onDismissMedicalDialog = onDismissMedicalDialog,
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

@Preview(showBackground = true)
@Composable
fun ProfileConnectionScreenPreview() {
    MaterialThemeComposeUI {
        ProfileConnectionScreen(
            googleAuthState = GoogleAuthUiState.Authenticated("usuario.synapse@gmail.com", "Atleta Synapse"),
            profileState = UserProfileUiState(),
            onGoogleLoginClick = {},
            onSignOutClick = {},
            onSelectAccountClick = {},
            onAddAnotherAccountClick = {},
            onDismissAccountSelection = {},
            onNavigateBackup = {},
            onNavigateDbExplorer = {},
            onFullNameChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onHeightCmChange = {},
            onBloodTypeChange = {},
            onMedicalConditionsChange = {},
            onSaveProfileClick = {},
        )
    }
}
