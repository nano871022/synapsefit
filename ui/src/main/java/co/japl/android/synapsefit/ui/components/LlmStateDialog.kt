@file:Suppress("UnusedPrivateMember")

package co.japl.android.synapsefit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmConfigState
import co.japl.android.synapsefit.core.domain.model.LlmErrorPayload
import co.japl.android.synapsefit.core.domain.model.LlmProvider

@Composable
fun LlmStateDialog(
    state: LlmConfigState,
    onDismissRequest: () -> Unit,
    onSelectConfig: (configId: String) -> Unit,
    onConfigureRedirect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is LlmConfigState.Ready) return

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                when (state) {
                    is LlmConfigState.Error -> {
                        ErrorDialogContent(
                            payload = state.payload,
                            onDismiss = onDismissRequest,
                        )
                    }

                    is LlmConfigState.MultiModelSelection -> {
                        MultiModelDialogContent(
                            configs = state.activeConfigs,
                            onSelectConfig = { configId ->
                                onSelectConfig(configId)
                                onDismissRequest()
                            },
                        )
                    }

                    is LlmConfigState.MissingConfig -> {
                        MissingConfigDialogContent(
                            onConfigureRedirect = {
                                onConfigureRedirect()
                                onDismissRequest()
                            },
                        )
                    }

                    LlmConfigState.Ready -> Unit
                }
            }
        }
    }
}

@Composable
private fun ErrorDialogContent(
    payload: LlmErrorPayload,
    onDismiss: () -> Unit,
) {
    Text(
        text = "${payload.code} - ${payload.status}",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.error,
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = payload.message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        NeonButton(
            text = "Aceptar",
            onClick = onDismiss,
        )
    }
}

@Composable
private fun MultiModelDialogContent(
    configs: List<LlmConfig>,
    onSelectConfig: (String) -> Unit,
) {
    var selectedId by remember { mutableStateOf(configs.firstOrNull()?.id ?: "") }

    Text(
        text = "Seleccionar Modelo LLM",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )

    Text(
        text = "Hay múltiples modelos activos configurados. Seleccione cuál desea utilizar:",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .selectableGroup(),
    ) {
        configs.forEach { config ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (config.id == selectedId),
                            onClick = { selectedId = config.id },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (config.id == selectedId),
                    onClick = null,
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                        ),
                )
                Text(
                    text = "${config.provider.name} (${config.modelName})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.small),
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        NeonButton(
            text = "Usar Modelo",
            onClick = { onSelectConfig(selectedId) },
        )
    }
}

@Composable
private fun MissingConfigDialogContent(onConfigureRedirect: () -> Unit) {
    Text(
        text = "Configuración Requerida",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )

    Text(
        text = "No hay ningún modelo LLM configurado. Debe configurar al menos un proveedor de IA para continuar.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        NeonButton(
            text = "Configurar LLM",
            onClick = onConfigureRedirect,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LlmStateDialogErrorPreview() {
    MaterialThemeComposeUI {
        LlmStateDialog(
            state =
                LlmConfigState.Error(
                    LlmErrorPayload(
                        code = "501",
                        status = "NOT_IMPLEMENTED",
                        message = "El método o servicio de IA no está implementado para la solicitud indicada.",
                    ),
                ),
            onDismissRequest = {},
            onSelectConfig = {},
            onConfigureRedirect = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LlmStateDialogMultiModelPreview() {
    MaterialThemeComposeUI {
        LlmStateDialog(
            state =
                LlmConfigState.MultiModelSelection(
                    activeConfigs =
                        listOf(
                            LlmConfig(
                                id = "1",
                                provider = LlmProvider.GEMINI,
                                apiKeyEncrypted = "key1",
                                modelName = "gemini-1.5-pro",
                                isActive = true,
                                createdAt = 0L,
                                updatedAt = 0L,
                            ),
                            LlmConfig(
                                id = "2",
                                provider = LlmProvider.OPENAI,
                                apiKeyEncrypted = "key2",
                                modelName = "gpt-4o",
                                isActive = true,
                                createdAt = 0L,
                                updatedAt = 0L,
                            ),
                        ),
                ),
            onDismissRequest = {},
            onSelectConfig = {},
            onConfigureRedirect = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LlmStateDialogMissingConfigPreview() {
    MaterialThemeComposeUI {
        LlmStateDialog(
            state = LlmConfigState.MissingConfig,
            onDismissRequest = {},
            onSelectConfig = {},
            onConfigureRedirect = {},
        )
    }
}
