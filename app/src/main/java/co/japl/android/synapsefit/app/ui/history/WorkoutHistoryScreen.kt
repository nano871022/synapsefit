@file:Suppress("FunctionNaming", "LongMethod", "MaxLineLength", "CyclomaticComplexMethod", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.history

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.history.ExerciseLogSetUiModel
import co.japl.android.synapsefit.app.controller.history.ExerciseSessionDetailUiModel
import co.japl.android.synapsefit.app.controller.history.SessionHistoryUiModel
import co.japl.android.synapsefit.app.controller.history.WorkoutHistoryUiState
import co.japl.android.synapsefit.app.controller.history.WorkoutSessionGroupUiModel
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.util.DateTimeUtils

@Composable
fun WorkoutHistoryScreen(
    state: WorkoutHistoryUiState,
    modifier: Modifier = Modifier,
) {
    var selectedGroup by remember { mutableStateOf<WorkoutSessionGroupUiModel?>(null) }

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.marginEdge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        item {
            Text(
                text = stringResource(R.string.workout_history),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        item {
            WeeklyStatsGrid(
                sessionsCount = state.weeklySessionsCount,
                totalVolumeKg = state.weeklyTotalVolumeKg,
            )
        }

        item {
            Text(
                text = stringResource(R.string.recent_records),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (state.sessionGroups.isEmpty() && state.recordedSessions.isEmpty()) {
            item {
                KineticCard {
                    Text(
                        text = stringResource(R.string.no_workout_records),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else if (state.sessionGroups.isNotEmpty()) {
            items(state.sessionGroups) { group ->
                WorkoutSessionGroupCard(
                    group = group,
                    onClick = { selectedGroup = group },
                )
            }
        } else {
            items(state.recordedSessions) { session ->
                WorkoutSessionHistoryCard(session = session)
            }
        }
    }

    selectedGroup?.let { group ->
        WorkoutSessionDetailDialog(
            group = group,
            onDismiss = { selectedGroup = null },
        )
    }
}

@Composable
fun WorkoutSessionGroupCard(
    group: WorkoutSessionGroupUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = group.sessionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.date_prefix, group.dateFormatted),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.exercises_completed_count, group.totalExercisesCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "${group.totalVolumeKg.toInt()} kg",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutHistoryScreenPreview() {
    MaterialThemeComposeUI {
        WorkoutHistoryScreen(
            state =
                WorkoutHistoryUiState(
                    weeklySessionsCount = 3,
                    weeklyTotalVolumeKg = 1250.0,
                    sessionGroups =
                        listOf(
                            WorkoutSessionGroupUiModel(
                                sessionId = "group_1",
                                sessionTitle = "Día 1",
                                dateFormatted = "2026-08-30",
                                timestamp = System.currentTimeMillis(),
                                totalExercisesCount = 2,
                                totalVolumeKg = 1250.0,
                                exercises =
                                    listOf(
                                        ExerciseSessionDetailUiModel(
                                            exerciseId = "ex_1",
                                            exerciseName = "Press de Banca",
                                            sets =
                                                listOf(
                                                    ExerciseLogSetUiModel(1, 10, 60.0, 120, System.currentTimeMillis()),
                                                    ExerciseLogSetUiModel(2, 10, 65.0, 125, System.currentTimeMillis()),
                                                ),
                                            averageReps = 10.0,
                                            averageWeightKg = 62.5,
                                        ),
                                    ),
                            ),
                        ),
                ),
        )
    }
}

@Composable
fun WorkoutSessionDetailDialog(
    group: WorkoutSessionGroupUiModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(MaterialTheme.spacing.medium)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text = group.sessionTitle + " — " + group.dateFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                val exCountStr = stringResource(R.string.exercises_completed_count, group.totalExercisesCount)
                val totalVolStr = stringResource(R.string.total_volume_stat)
                Text(
                    text = "$exCountStr | $totalVolStr: ${group.totalVolumeKg} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                group.exercises.forEach { exercise ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = exercise.exerciseName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        exercise.sets.forEach { setItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(R.string.set_col) + " ${setItem.setIndex}:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "${setItem.repsCompleted} reps x ${setItem.weightLiftedKg} kg",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.exercise_averages_prefix, exercise.averageReps, exercise.averageWeightKg),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyStatsGrid(
    sessionsCount: Int,
    totalVolumeKg: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.records_stat).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "$sessionsCount",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.total_volume_stat).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${totalVolumeKg.toInt()} kg",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun WorkoutSessionHistoryCard(
    session: SessionHistoryUiModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                if (session.exerciseName.isNotBlank()) {
                    Text(
                        text = session.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "${session.repsCompleted} reps x ${session.weightLiftedKg} kg",
                    style =
                        if (session.exerciseName.isNotBlank()) {
                            MaterialTheme.typography.bodyMedium
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                    color =
                        if (session.exerciseName.isNotBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
                Text(
                    text = stringResource(R.string.date_prefix, DateTimeUtils.formatEpoch(session.timestamp, "yyyy-MM-dd")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = session.sourceDevice,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
