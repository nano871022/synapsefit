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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    onFinishSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NeonButton(
                text = stringResource(R.string.finish_workout),
                onClick = onFinishSession,
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(MaterialTheme.spacing.medium)
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

            SetTrackingTable(
                sets = state.sets,
                onRepsChange = onSetRepsChange,
                onWeightChange = onSetWeightChange,
                onCompleteSet = onCompleteSet,
            )
        }
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
                style = MaterialTheme.typography.headlineLarge,
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
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.rest_between_sets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
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
fun SetTrackingTable(
    sets: List<WorkoutSetUiModel>,
    onRepsChange: (Int, String) -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onCompleteSet: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.set_col),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(48.dp),
            )
            Text(
                text = stringResource(R.string.reps_col),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.weight_col),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.done_col),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(48.dp),
            )
        }

        sets.forEach { setItem ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${setItem.setIndex}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(48.dp),
                )

                OutlinedTextField(
                    value = setItem.repsCompleted,
                    onValueChange = { onRepsChange(setItem.setIndex, it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !setItem.isCompleted,
                )

                OutlinedTextField(
                    value = setItem.weightLiftedKg,
                    onValueChange = { onWeightChange(setItem.setIndex, it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = !setItem.isCompleted,
                )

                Checkbox(
                    checked = setItem.isCompleted,
                    onCheckedChange = { if (it) onCompleteSet(setItem.setIndex) },
                    enabled = !setItem.isCompleted,
                    modifier = Modifier.width(48.dp),
                )
            }
        }
    }
}
