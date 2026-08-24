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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLMSettingsScreen(
    state: LlmSettingsUiState,
    onSaveConfig: (LlmProvider, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedProvider by remember { mutableStateOf(LlmProvider.GEMINI) }
    var apiKeyInput by remember { mutableStateOf("") }
    var modelNameInput by remember { mutableStateOf("gemini-1.5-flash") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.llm_providers_config),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        SecurityBadge()

        Text(
            text = stringResource(R.string.configure_credentials),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedProvider.name,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.llm_provider)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                LlmProvider.entries.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.name) },
                        onClick = {
                            selectedProvider = p
                            modelNameInput =
                                when (p) {
                                    LlmProvider.GEMINI -> "gemini-1.5-flash"
                                    LlmProvider.OPENAI -> "gpt-4o-mini"
                                    LlmProvider.ANTHROPIC -> "claude-3-haiku-20240307"
                                }
                            expanded = false
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text(stringResource(R.string.api_key)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = modelNameInput,
            onValueChange = { modelNameInput = it },
            label = { Text(stringResource(R.string.model_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        NeonButton(
            text = stringResource(R.string.save_and_activate_provider),
            onClick = {
                if (apiKeyInput.isNotBlank()) {
                    onSaveConfig(selectedProvider, apiKeyInput, modelNameInput)
                    apiKeyInput = ""
                }
            },
            enabled = apiKeyInput.isNotBlank(),
        )

        if (state.providers.isNotEmpty()) {
            Text(
                text = stringResource(R.string.registered_providers),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            state.providers.forEach { providerModel ->
                ProviderCard(providerModel = providerModel)
            }
        }
    }
}

@Composable
fun SecurityBadge(modifier: Modifier = Modifier) {
    KineticCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.security_keystore_badge),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ProviderCard(
    providerModel: LlmProviderUiModel,
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
                    text = providerModel.provider.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.model_prefix, providerModel.modelName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.key_prefix, providerModel.apiKeyMasked),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (providerModel.isActive) {
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(stringResource(R.string.active_badge), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
