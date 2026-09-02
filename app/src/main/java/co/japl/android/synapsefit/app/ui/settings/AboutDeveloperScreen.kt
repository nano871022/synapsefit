@file:Suppress("FunctionNaming", "LongMethod", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.settings.AboutDeveloperUiState
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun AboutDeveloperScreen(
    state: AboutDeveloperUiState,
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
            text = stringResource(R.string.about_synapsefit),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        AppVersionCard(
            versionName = state.versionName,
            versionCode = state.versionCode,
            applicationId = state.applicationId,
        )

        ModuleArchitectureSummary()
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutDeveloperScreenPreview() {
    SynapseFitTheme {
        AboutDeveloperScreen(
            state =
                AboutDeveloperUiState(
                    versionName = "1.0.0",
                    versionCode = 1,
                    applicationId = "co.japl.android.synapsefit",
                ),
        )
    }
}

@Composable
fun AppVersionCard(
    versionName: String,
    versionCode: Long,
    applicationId: String,
    modifier: Modifier = Modifier,
) {
    KineticCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.app_info),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.version_label, versionName, versionCode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.id_label, applicationId),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ModuleArchitectureSummary(modifier: Modifier = Modifier) {
    KineticCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.hexagonal_architecture),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.hexagonal_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.data_sovereignty),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
