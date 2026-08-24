@file:Suppress("FunctionNaming", "LongMethod", "UnusedParameter")

package co.japl.android.synapsefit.app.ui.measurements

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.ui.components.CanvasTrendGraph
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.theme.spacing
import co.japl.android.synapsefit.util.MathUtils

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
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.trend_analysis),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        MetricSelectorChipRow(
            selectedMetric = state.selectedMetric,
            onMetricSelected = onMetricSelected,
        )

        KineticCard {
            Text(
                text = stringResource(R.string.evolution, getZoneLabel(state.selectedMetric)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.average, MathUtils.roundToDecimals(state.averageValue, 1).toString()),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )

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
                label = { Text(getZoneLabel(zone)) },
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
