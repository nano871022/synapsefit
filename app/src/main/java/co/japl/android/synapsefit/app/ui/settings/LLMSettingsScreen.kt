@file:Suppress(
    "FunctionNaming",
    "LongMethod",
    "CyclomaticComplexMethod",
    "LongParameterList",
    "MaxLineLength",
    "UnusedPrivateMember",
    "MagicNumber",
)

package co.japl.android.synapsefit.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.settings.LlmProviderUiModel
import co.japl.android.synapsefit.app.controller.settings.LlmSettingsUiState
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.ui.components.NeonButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLMSettingsScreen(
    state: LlmSettingsUiState,
    onSaveConfig: (LlmProvider, String, String) -> Unit,
    onFetchModels: (LlmProvider, String) -> Unit,
    onActivateConfig: (String) -> Unit = {},
    onDeactivateConfig: (String) -> Unit = {},
    onDuplicateConfig: (String) -> Unit = {},
    onDeleteConfig: (String) -> Unit = {},
    onUpdateConfig: (String, LlmProvider, String, String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    var isFormDialogOpen by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var deletingId by remember { mutableStateOf<String?>(null) }
    var selectedProvider by remember { mutableStateOf(LlmProvider.GEMINI) }
    var apiKeyInput by remember { mutableStateOf("") }
    var modelNameInput by remember { mutableStateOf("") }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.llm_providers_config),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        SecurityBadge()

        NeonButton(
            text = stringResource(R.string.add_provider),
            onClick = {
                editingId = null
                apiKeyInput = ""
                modelNameInput = ""
                isFormDialogOpen = true
            },
        )

        if (state.isLoading && state.providers.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        Text(
            text = stringResource(R.string.registered_providers),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        state.providers.forEach { providerModel ->
            ProviderCard(
                providerModel = providerModel,
                onActivate = { onActivateConfig(providerModel.id) },
                onDeactivate = { onDeactivateConfig(providerModel.id) },
                onDuplicate = { onDuplicateConfig(providerModel.id) },
                onDelete = { deletingId = providerModel.id },
                onEdit = {
                    editingId = providerModel.id
                    selectedProvider = providerModel.provider
                    apiKeyInput = ""
                    modelNameInput = providerModel.modelName
                    isFormDialogOpen = true
                },
            )
        }
    }

    if (deletingId != null) {
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingId?.let { onDeleteConfig(it) }
                        deletingId = null
                    },
                ) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingId = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (isFormDialogOpen) {
        LlmFormDialog(
            editingId = editingId,
            selectedProvider = selectedProvider,
            apiKeyInput = apiKeyInput,
            modelNameInput = modelNameInput,
            availableModels = state.availableModels,
            isLoading = state.isLoading,
            onProviderSelected = { selectedProvider = it },
            onApiKeyChange = { apiKeyInput = it },
            onModelNameChange = { modelNameInput = it },
            onFetchModels = { p, key -> onFetchModels(p, key) },
            onSave = {
                val currentId = editingId
                if (currentId != null) {
                    onUpdateConfig(currentId, selectedProvider, apiKeyInput, modelNameInput)
                } else {
                    onSaveConfig(selectedProvider, apiKeyInput, modelNameInput)
                }
                isFormDialogOpen = false
            },
            onDismiss = { isFormDialogOpen = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LLMSettingsScreenPreview() {
    MaterialThemeComposeUI {
        LLMSettingsScreen(
            state =
                LlmSettingsUiState(
                    providers =
                        listOf(
                            LlmProviderUiModel(
                                id = "1",
                                provider = LlmProvider.GEMINI,
                                apiKeyMasked = "••••••••1234",
                                modelName = "gemini-1.5-flash",
                                isActive = true,
                            ),
                        ),
                ),
            onSaveConfig = { _, _, _ -> },
            onFetchModels = { _, _ -> },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LlmFormDialog(
    editingId: String?,
    selectedProvider: LlmProvider,
    apiKeyInput: String,
    modelNameInput: String,
    availableModels: List<String>,
    isLoading: Boolean,
    onProviderSelected: (LlmProvider) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onModelNameChange: (String) -> Unit,
    onFetchModels: (LlmProvider, String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var providerExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }

    val defaultModel =
        when (selectedProvider) {
            LlmProvider.GEMINI -> "gemini-1.5-flash"
            LlmProvider.OPENAI -> "gpt-4o-mini"
            LlmProvider.ANTHROPIC -> "claude-3-haiku-20240307"
        }

    androidx.compose.runtime.LaunchedEffect(selectedProvider) {
        if (availableModels.isEmpty() && modelNameInput.isBlank()) {
            onModelNameChange(defaultModel)
        }
    }

    androidx.compose.runtime.LaunchedEffect(availableModels) {
        if (availableModels.isNotEmpty()) {
            onModelNameChange(availableModels.first())
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text =
                        if (editingId != null) {
                            stringResource(
                                R.string.update_provider,
                            )
                        } else {
                            stringResource(R.string.configure_credentials)
                        },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                ExposedDropdownMenuBox(
                    expanded = providerExpanded,
                    onExpandedChange = { providerExpanded = !providerExpanded },
                ) {
                    OutlinedTextField(
                        value = selectedProvider.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.llm_provider)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = providerExpanded,
                        onDismissRequest = { providerExpanded = false },
                    ) {
                        LlmProvider.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    onProviderSelected(p)
                                    providerExpanded = false
                                },
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = onApiKeyChange,
                        label = { Text(stringResource(R.string.api_key)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    NeonButton(
                        text = "Fetch",
                        onClick = { onFetchModels(selectedProvider, apiKeyInput) },
                        enabled = apiKeyInput.isNotBlank() && !isLoading,
                        modifier = Modifier.width(100.dp),
                    )
                }

                if (availableModels.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = modelExpanded,
                        onExpandedChange = { modelExpanded = !modelExpanded },
                    ) {
                        OutlinedTextField(
                            value = modelNameInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.model_name)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = modelExpanded,
                            onDismissRequest = { modelExpanded = false },
                        ) {
                            availableModels.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        onModelNameChange(model)
                                        modelExpanded = false
                                    },
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = modelNameInput,
                        onValueChange = onModelNameChange,
                        label = { Text(stringResource(R.string.model_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                NeonButton(
                    text =
                        if (editingId != null) {
                            stringResource(
                                R.string.update_provider,
                            )
                        } else {
                            stringResource(R.string.save_and_activate_provider)
                        },
                    onClick = onSave,
                    enabled = apiKeyInput.isNotBlank() || editingId != null,
                    isLoading = isLoading,
                )
            }
        }
    }
}

@Composable
fun SecurityBadge(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
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
    onActivate: () -> Unit = {},
    onDeactivate: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (providerModel.isActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
            ),
        border =
            if (providerModel.isActive) {
                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                null
            },
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = providerModel.provider.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (providerModel.isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (providerModel.isActive) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Text(
                                text = stringResource(R.string.active_badge),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }
                    Switch(
                        checked = providerModel.isActive,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                onActivate()
                            } else {
                                onDeactivate()
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            ) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.edit))
                }
                TextButton(onClick = onDuplicate) {
                    Text(stringResource(R.string.duplicate))
                }
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
