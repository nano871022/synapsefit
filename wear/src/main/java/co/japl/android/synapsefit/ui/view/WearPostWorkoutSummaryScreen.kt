package co.japl.android.synapsefit.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainer
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.history.ExerciseHistory
import co.japl.android.synapsefit.ui.viewmodel.WearPostWorkoutSummaryUiState

@Composable
fun WearPostWorkoutSummaryScreen(
    uiState: WearPostWorkoutSummaryUiState,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize().background(BackgroundDark),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.wear_summary_title),
                        style = MaterialTheme.typography.caption1,
                        color = OnSurfaceDark.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                    )
                    if (uiState.planTitle.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.wear_day_header, uiState.day, uiState.planTitle),
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Sync Status Badge
            item {
                SyncStatusBadge(
                    isSynced = uiState.isSyncedWithPhone && uiState.pendingSyncDataCount == 0,
                )
            }

            // Metrics Grid / Cards
            item {
                SummaryMetricRow(
                    label1 = stringResource(R.string.wear_summary_total_time),
                    value1 = uiState.formattedDuration,
                    label2 = stringResource(R.string.wear_summary_total_volume),
                    value2 = stringResource(R.string.wear_summary_kg, uiState.totalVolumeKg),
                )
            }

            // Heart Rate Metric Card
            if (uiState.averageHeartRateBpm != null || uiState.maxHeartRateBpm != null) {
                item {
                    HeartRateSummaryCard(
                        avgHr = uiState.averageHeartRateBpm,
                        maxHr = uiState.maxHeartRateBpm,
                    )
                }
            }

            // Completed Exercises Header
            if (uiState.exercises.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.wear_summary_completed_exercises),
                        style = MaterialTheme.typography.caption2,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                }

                items(uiState.exercises) { exercise ->
                    ExerciseSummaryCardItem(exercise = exercise)
                }
            }

            // Finish Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onFinish,
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryCyan),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.wear_summary_finish),
                        style = MaterialTheme.typography.button,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryDark,
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncStatusBadge(isSynced: Boolean) {
    val backgroundColor = if (isSynced) SurfaceContainerHigh else SurfaceContainer
    val iconColor = if (isSynced) PrimaryCyan else OnSurfaceDark.copy(alpha = 0.6f)
    val textRes = if (isSynced) R.string.wear_synced else R.string.wear_summary_saved_locally

    Chip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceDark,
            )
        },
        icon = {
            Icon(
                imageVector = if (isSynced) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp),
            )
        },
        colors =
            ChipDefaults.chipColors(
                backgroundColor = backgroundColor,
                disabledBackgroundColor = backgroundColor,
            ),
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

@Composable
private fun SummaryMetricRow(
    label1: String,
    value1: String,
    label2: String,
    value2: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        MetricBox(
            label = label1,
            value = value1,
            modifier = Modifier.weight(1f),
        )
        MetricBox(
            label = label2,
            value = value2,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .background(SurfaceContainerHigh, shape = RoundedCornerShape(12.dp))
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = OnSurfaceDark.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HeartRateSummaryCard(
    avgHr: Int?,
    maxHr: Int?,
) {
    TitleCard(
        onClick = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.wear_bpm_unit),
                    style = MaterialTheme.typography.caption1,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark,
                )
            }
        },
        backgroundPainter =
            CardDefaults.cardBackgroundPainter(
                startBackgroundColor = SurfaceContainerHigh,
                endBackgroundColor = SurfaceContainerHigh,
            ),
        contentColor = OnSurfaceDark,
        titleColor = OnSurfaceDark,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (avgHr != null) {
                Text(
                    text = "${stringResource(R.string.wear_summary_avg_hr)}: $avgHr",
                    style = MaterialTheme.typography.caption2,
                    color = OnSurfaceDark,
                )
            }
            if (maxHr != null) {
                Text(
                    text = "${stringResource(R.string.wear_summary_max_hr)}: $maxHr",
                    style = MaterialTheme.typography.caption2,
                    color = OnSurfaceDark,
                )
            }
        }
    }
}

@Composable
private fun ExerciseSummaryCardItem(exercise: ExerciseHistory) {
    TitleCard(
        onClick = {},
        title = {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        time = {
            Text(
                text = stringResource(R.string.wear_summary_sets_count, exercise.sets.size),
                style = MaterialTheme.typography.caption2,
                color = PrimaryCyan,
            )
        },
        backgroundPainter =
            CardDefaults.cardBackgroundPainter(
                startBackgroundColor = SurfaceContainer,
                endBackgroundColor = SurfaceContainer,
            ),
        contentColor = OnSurfaceDark,
        titleColor = OnSurfaceDark,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        if (exercise.muscleGroup.isNotBlank()) {
            Text(
                text = exercise.muscleGroup,
                style = MaterialTheme.typography.caption2,
                color = OnSurfaceDark.copy(alpha = 0.6f),
            )
        }
    }
}
