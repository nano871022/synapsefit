@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList", "MagicNumber", "MaxLineLength", "ImplicitDefaultLocale")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.HeartRateGauge
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun ActiveWorkoutSessionScreen(
    state: ActiveWorkoutUiState,
    onSetRepsChange: (setIndex: Int, reps: String) -> Unit,
    onSetWeightChange: (setIndex: Int, weight: String) -> Unit,
    onCompleteSet: (setIndex: Int) -> Unit,
    onNextSetOrExercise: () -> Unit = {},
    onFinishSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NeonButton(
                text = stringResource(R.string.finish_workout),
                onClick = onFinishSession,
                modifier = Modifier.padding(MaterialTheme.spacing.marginEdge),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(MaterialTheme.spacing.marginEdge)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = state.planTitle.ifBlank { stringResource(R.string.live_session) },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WorkoutChronometer(elapsedTimeSeconds = state.elapsedTimeSeconds)

                state.heartRateBpm?.let { bpm ->
                    HeartRateGauge(heartRateBpm = bpm)
                }
            }

            state.restTimerSecondsRemaining?.let { sec ->
                RestTimerWidget(secondsRemaining = sec)
            }

            Text(
                text = stringResource(R.string.exercise_prefix, state.currentExerciseName),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
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
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    Text(
                        text = stringResource(R.string.set_label, state.currentSetIndex, state.totalSetsForCurrentExercise),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = stringResource(R.string.target_reps, state.targetRepsForCurrentSet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    OutlinedTextField(
                        value = state.currentSetWeightKg,
                        onValueChange = { onSetWeightChange(state.currentSetIndex, it) },
                        label = { Text(stringResource(R.string.weight_col)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !state.isCurrentSetCompleted,
                    )

                    OutlinedTextField(
                        value = state.currentSetReps,
                        onValueChange = { onSetRepsChange(state.currentSetIndex, it) },
                        label = { Text(stringResource(R.string.reps_col)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !state.isCurrentSetCompleted,
                    )

                    if (!state.isCurrentSetCompleted) {
                        NeonButton(
                            text = stringResource(R.string.complete_set),
                            onClick = { onCompleteSet(state.currentSetIndex) },
                            enabled = state.currentSetReps.isNotBlank(),
                        )
                    } else {
                        NeonButton(
                            text = stringResource(R.string.next_set_or_exercise),
                            onClick = onNextSetOrExercise,
                        )
                    }
                }
            }
        }
    }

    if (state.isSessionComplete && state.summary != null) {
        WorkoutSummaryDialog(
            summary = state.summary,
            onDismiss = onFinishSession,
        )
    }
}

@Composable
fun WorkoutChronometer(
    elapsedTimeSeconds: Long,
    modifier: Modifier = Modifier,
) {
    val minutes = elapsedTimeSeconds / 60
    val seconds = elapsedTimeSeconds % 60
    val formatted = String.format("%02d:%02d", minutes, seconds)

    KineticCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.elapsed_time),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = formatted,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun RestTimerWidget(
    secondsRemaining: Int,
    modifier: Modifier = Modifier,
) {
    KineticCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.rest_between_sets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${secondsRemaining}s",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun WorkoutSummaryDialog(
    summary: WorkoutSummary,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
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
                    text = stringResource(R.string.workout_summary),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                val mins = summary.totalTimeSeconds / 60
                val secs = summary.totalTimeSeconds % 60
                val timeFormatted = String.format("%02d:%02d", mins, secs)

                Text(
                    text = "${stringResource(R.string.total_workout_time)}: $timeFormatted",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                HorizontalDivider()

                summary.exerciseSummaries.forEach { item ->
                    val exMins = item.timeSpentSeconds / 60
                    val exSecs = item.timeSpentSeconds % 60
                    val exTimeFormatted = String.format("%02d:%02d", exMins, exSecs)

                    Text(
                        text =
                            stringResource(
                                R.string.exercise_summary_item,
                                item.exerciseName,
                                item.totalSetsCompleted,
                                item.maxWeightLiftedKg.toString(),
                                exTimeFormatted,
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                NeonButton(
                    text = stringResource(R.string.accept_and_close),
                    onClick = onDismiss,
                )
            }
        }
    }
}
