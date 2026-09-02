@file:Suppress("FunctionNaming", "LongMethod", "MaxLineLength", "UnusedParameter", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme
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
                .padding(MaterialTheme.spacing.marginEdge)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        SyncStatusHeader(
            userName = state.userName,
            isSyncing = state.isSyncing,
            onProfileClick = onProfileClick,
        )

        WelcomeHeader(userName = state.userName)

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
    }
}

@Composable
fun WelcomeHeader(
    userName: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.hello_user, userName),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
        Text(
            text = "Listo para alcanzar tus objetivos de hoy.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                text = stringResource(R.string.nav_dashboard),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
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

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    SynapseFitTheme {
        DashboardScreen(
            state =
                DashboardUiState(
                    userName = "Atleta SynapseFit",
                    latestWeightKg = 75.0,
                    weightTrendDeltaKg = -0.5,
                    todayWorkoutTitle = "Día 1 - Pecho y Tríceps",
                    todayWorkoutPlanId = "plan_1",
                ),
            onStartWorkoutClick = {},
            onLogMeasurementClick = {},
            onProfileClick = {},
        )
    }
}

@Composable
fun LatestMeasurementCard(
    latestWeightKg: Double?,
    deltaWeightKg: Double?,
    onLogClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.latest_weight_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (latestWeightKg != null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${MathUtils.roundToDecimals(latestWeightKg, 1)}",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onTertiary,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.weight_unit),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_records),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    deltaWeightKg?.let { delta ->
                        val isNegative = delta < 0
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.1f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = if (isNegative) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isNegative) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.error,
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${MathUtils.roundToDecimals(delta, 1)}kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isNegative) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                    IconButton(onClick = onLogClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.log_measurement),
                            tint = MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.today_workout_title).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
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
