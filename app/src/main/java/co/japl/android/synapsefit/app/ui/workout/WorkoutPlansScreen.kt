@file:Suppress("FunctionNaming", "LongMethod")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun WorkoutPlansScreen(
    state: WorkoutPlansUiState,
    onPlanClick: (planId: String) -> Unit,
    onGeneratePlanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onGeneratePlanClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Text(stringResource(R.string.generate_with_ai))
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            item {
                Text(
                    text = stringResource(R.string.workout_plans),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                Text(
                    text = stringResource(R.string.active_plan),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            item {
                if (state.activePlan != null) {
                    ActivePlanCard(
                        plan = state.activePlan,
                        onPlanClick = onPlanClick,
                    )
                } else {
                    KineticCard {
                        Text(
                            text = stringResource(R.string.no_active_plan_configured),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (state.archivedPlans.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.plan_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                items(state.archivedPlans) { plan ->
                    ArchivedPlanItem(
                        plan = plan,
                        onPlanClick = onPlanClick,
                    )
                }
            }
        }
    }
}

@Composable
fun ActivePlanCard(
    plan: WorkoutPlanSummary,
    onPlanClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onPlanClick(plan.id) },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                if (plan.generatedByLlm) {
                    Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                        Text(stringResource(R.string.ia_generated), color = MaterialTheme.colorScheme.onTertiary)
                    }
                }
            }

            Text(
                text = plan.goalDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.exercises_programmed, plan.totalExercises),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun ArchivedPlanItem(
    plan: WorkoutPlanSummary,
    onPlanClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    KineticCard(
        modifier = modifier.clickable { onPlanClick(plan.id) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = plan.goalDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.view_detail),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
