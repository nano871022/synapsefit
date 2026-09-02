@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList", "MaxLineLength", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.measurements.BodyMeasurementsUiState
import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.ui.components.AnatomicalInputField
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.util.DateTimeUtils

@Composable
fun BodyMeasurementsScreen(
    state: BodyMeasurementsUiState,
    onWeightChange: (String) -> Unit,
    onChestChange: (String) -> Unit,
    onWaistChange: (String) -> Unit,
    onHipChange: (String) -> Unit,
    onBicepLeftChange: (String) -> Unit,
    onBicepRightChange: (String) -> Unit,
    onThighLeftChange: (String) -> Unit,
    onThighRightChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onViewGraphClick: () -> Unit,
    onOpenPopupClick: () -> Unit = {},
    onClosePopupClick: () -> Unit = {},
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.anthropometric_record),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            OutlinedButton(
                onClick = onOpenPopupClick,
                modifier = Modifier.weight(1f).height(40.dp),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Medidas", style = MaterialTheme.typography.labelMedium)
            }

            OutlinedButton(
                onClick = onViewGraphClick,
                modifier = Modifier.weight(1f).height(40.dp),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver Gráfico", style = MaterialTheme.typography.labelMedium)
            }
        }

        Text(
            text = "Historial de Medidas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (state.history.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
            ) {
                Text(
                    text = "No hay registros de medidas guardados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                )
            }
        } else {
            state.history.forEach { measurement ->
                MeasurementHistoryItem(measurement = measurement)
            }
        }
    }

    if (state.isPopupOpen) {
        Dialog(onDismissRequest = onClosePopupClick) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(MaterialTheme.spacing.medium)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Nueva Entrada de Medidas",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        TextButton(onClick = onClosePopupClick) {
                            Text("Cerrar")
                        }
                    }

                    state.errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    ) {
                        AnatomicalInputField(
                            value = state.weightKg,
                            onValueChange = onWeightChange,
                            label = stringResource(R.string.weight_corp),
                            unitLabel = stringResource(R.string.weight_unit),
                            isError = state.errorMessage != null && state.weightKg.isBlank(),
                            modifier = Modifier.weight(1f),
                        )
                        AnatomicalInputField(
                            value = state.chestCm,
                            onValueChange = onChestChange,
                            label = stringResource(R.string.chest),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    ) {
                        AnatomicalInputField(
                            value = state.waistCm,
                            onValueChange = onWaistChange,
                            label = stringResource(R.string.waist),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                        AnatomicalInputField(
                            value = state.hipCm,
                            onValueChange = onHipChange,
                            label = stringResource(R.string.hip),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    ) {
                        AnatomicalInputField(
                            value = state.bicepLeftCm,
                            onValueChange = onBicepLeftChange,
                            label = stringResource(R.string.bicep_left),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                        AnatomicalInputField(
                            value = state.bicepRightCm,
                            onValueChange = onBicepRightChange,
                            label = stringResource(R.string.bicep_right),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    ) {
                        AnatomicalInputField(
                            value = state.thighLeftCm,
                            onValueChange = onThighLeftChange,
                            label = stringResource(R.string.thigh_left),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                        AnatomicalInputField(
                            value = state.thighRightCm,
                            onValueChange = onThighRightChange,
                            label = stringResource(R.string.thigh_right),
                            unitLabel = stringResource(R.string.cm_unit),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = onNotesChange,
                        label = { Text(stringResource(R.string.notes_observations)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )

                    NeonButton(
                        text = stringResource(R.string.save_measurements),
                        onClick = onSaveClick,
                        isLoading = state.isSaving,
                        enabled = state.weightKg.isNotBlank(),
                    )
                }
            }
        }
    }
}

@Composable
fun MeasurementHistoryItem(
    measurement: BodyMeasurement,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = DateTimeUtils.formatEpoch(measurement.createdAt, "dd MMM, yyyy"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${measurement.weightKg} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                measurement.chestCm?.let {
                    MeasurementSmallStat(label = "Pecho", value = "$it cm")
                }
                measurement.waistCm?.let {
                    MeasurementSmallStat(label = "Cintura", value = "$it cm")
                }
                measurement.hipCm?.let {
                    MeasurementSmallStat(label = "Cadera", value = "$it cm")
                }
            }
        }
    }
}

@Composable
fun MeasurementSmallStat(
    label: String,
    value: String,
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyMeasurementsScreenPreview() {
    MaterialThemeComposeUI {
        BodyMeasurementsScreen(
            state =
                BodyMeasurementsUiState(
                    history =
                        listOf(
                            BodyMeasurement(
                                id = "1",
                                weightKg = 75.0,
                                chestCm = 100.0,
                                waistCm = 80.0,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                            ),
                        ),
                ),
            onWeightChange = {},
            onChestChange = {},
            onWaistChange = {},
            onHipChange = {},
            onBicepLeftChange = {},
            onBicepRightChange = {},
            onThighLeftChange = {},
            onThighRightChange = {},
            onNotesChange = {},
            onSaveClick = {},
            onViewGraphClick = {},
        )
    }
}
