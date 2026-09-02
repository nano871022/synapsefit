@file:Suppress("FunctionNaming", "LongMethod", "UnusedParameter", "MagicNumber", "MaxLineLength", "UnusedPrivateMember")

package co.japl.android.synapsefit.app.ui.measurements

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.ui.components.CanvasTrendGraph
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme
import co.japl.android.synapsefit.ui.theme.spacing
import co.japl.android.synapsefit.util.MathUtils

private const val DAYS_30 = 30
private const val DAYS_90 = 90
private const val DAYS_180 = 180
private const val DAYS_365 = 365

@Composable
fun MeasurementProgressGraphScreen(
    state: MeasurementProgressUiState,
    onMetricSelected: (AnatomicalZone) -> Unit,
    onTimeRangeSelected: (Int) -> Unit,
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
            text = stringResource(R.string.trend_analysis),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        TimeRangeSelectorRow(
            selectedDays = state.timeRangeDays,
            onTimeRangeSelected = onTimeRangeSelected,
        )

        MetricSelectorChipRow(
            selectedMetric = state.selectedMetric,
            onMetricSelected = onMetricSelected,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                Text(
                    text = getZoneLabel(state.selectedMetric).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.evolution, getZoneLabel(state.selectedMetric)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val avgFormatted = MathUtils.roundToDecimals(state.averageValue, 1).toString()
                    Text(
                        text = stringResource(R.string.average, avgFormatted),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                if (state.dataPoints.isEmpty()) {
                    Text(
                        text = stringResource(R.string.not_enough_data),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(MaterialTheme.spacing.large),
                    )
                } else {
                    CanvasTrendGraph(dataPoints = state.dataPoints)
                }
            }
        }
    }
}

@Composable
fun TimeRangeSelectorRow(
    selectedDays: Int,
    onTimeRangeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf("1M" to DAYS_30, "3M" to DAYS_90, "6M" to DAYS_180, "1A" to DAYS_365, "Todo" to 0)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        options.forEach { (label, days) ->
            FilterChip(
                selected = days == selectedDays,
                onClick = { onTimeRangeSelected(days) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        }
    }
}

@Composable
fun MetricSelectorChipRow(
    selectedMetric: AnatomicalZone,
    onMetricSelected: (AnatomicalZone) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        AnatomicalZone.entries.forEach { zone ->
            FilterChip(
                selected = zone == selectedMetric,
                onClick = { onMetricSelected(zone) },
                label = { Text(getZoneLabel(zone), style = MaterialTheme.typography.labelSmall) },
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        }
    }
}

@Composable
private fun getZoneLabel(zone: AnatomicalZone): String {
    return when (zone) {
        AnatomicalZone.WEIGHT -> stringResource(R.string.zone_weight)
        AnatomicalZone.CHEST -> stringResource(R.string.zone_chest)
        AnatomicalZone.WAIST -> stringResource(R.string.zone_waist)
        AnatomicalZone.HIP -> stringResource(R.string.zone_hip)
        AnatomicalZone.BICEP_LEFT -> stringResource(R.string.zone_bicep_left)
        AnatomicalZone.BICEP_RIGHT -> stringResource(R.string.zone_bicep_right)
        AnatomicalZone.THIGH_LEFT -> stringResource(R.string.zone_thigh_left)
        AnatomicalZone.THIGH_RIGHT -> stringResource(R.string.zone_thigh_right)
    }
}

@Preview(showBackground = true)
@Composable
private fun MeasurementProgressGraphScreenPreview() {
    SynapseFitTheme {
        MeasurementProgressGraphScreen(
            state =
                MeasurementProgressUiState(
                    selectedMetric = AnatomicalZone.WEIGHT,
                    averageValue = 74.5,
                ),
            onMetricSelected = {},
            onTimeRangeSelected = {},
        )
    }
}
