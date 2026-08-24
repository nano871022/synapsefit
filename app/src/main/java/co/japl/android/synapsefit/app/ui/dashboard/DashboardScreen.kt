@file:Suppress("FunctionNaming", "LongMethod", "MaxLineLength")

package co.japl.android.synapsefit.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import co.japl.android.synapsefit.util.MathUtils

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onStartWorkoutClick: (planId: String) -> Unit,
    onLogMeasurementClick: () -> Unit,
    onProfileClick: () -> Unit,
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
        SyncStatusHeader(
            userName = state.userName,
            isSyncing = state.isSyncing,
            onProfileClick = onProfileClick,
        )

        LatestMeasurementCard(
            latestWeightKg = state.latestWeightKg,
            deltaWeightKg = state.weightTrendDeltaKg,
            onLogClick = onLogMeasurementClick,
        )

        TodayWorkoutCard(
            title = state.todayWorkoutTitle ?: stringResource(R.string.no_active_routine),
            planId = state.todayWorkoutPlanId,
            onStartWorkout = { planId ->
                if (planId != null) {
                    onStartWorkoutClick(planId)
                }
            },
        )

        QuickActionsRow(
            onLogMeasurement = onLogMeasurementClick,
            onStartWorkout = {
                state.todayWorkoutPlanId?.let { onStartWorkoutClick(it) }
            },
            hasActivePlan = state.todayWorkoutPlanId != null,
        )
    }
}

@Composable
fun SyncStatusHeader(
    userName: String,
    isSyncing: Boolean,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.hello_user, userName),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = if (isSyncing) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (isSyncing) stringResource(R.string.syncing) else stringResource(R.string.synced),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onProfileClick) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.profile),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun LatestMeasurementCard(
    latestWeightKg: Double?,
    deltaWeightKg: Double?,
    onLogClick: () -> Unit,
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
                    text = stringResource(R.string.latest_weight_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (latestWeightKg != null) {
                    Text(
                        text = "${MathUtils.roundToDecimals(latestWeightKg, 1)} ${stringResource(R.string.weight_unit)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    deltaWeightKg?.let { delta ->
                        val sign = if (delta >= 0) "+" else ""
                        Text(
                            text = stringResource(R.string.tendency, "$sign${MathUtils.roundToDecimals(delta, 1)}"),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (delta <= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_records),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onLogClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.log_measurement),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun TodayWorkoutCard(
    title: String,
    planId: String?,
    onStartWorkout: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.today_workout_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            NeonButton(
                text = stringResource(R.string.start_session),
                onClick = { onStartWorkout(planId) },
                enabled = planId != null,
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    onLogMeasurement: () -> Unit,
    onStartWorkout: () -> Unit,
    hasActivePlan: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        OutlinedButton(
            onClick = onLogMeasurement,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(R.string.log_weight))
        }
        OutlinedButton(
            onClick = onStartWorkout,
            enabled = hasActivePlan,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(R.string.live_session))
        }
    }
}
