package co.japl.android.synapsefit.ui.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.ErrorContainerDark
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.com.japl.ui.theme.SurfaceContainerLow
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutUiState
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import java.util.Locale

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3600
private const val CHIP_WIDTH_FRACTION = 0.85f

@Suppress("LongMethod", "LongParameterList", "CyclomaticComplexMethod")
@Composable
fun WearActiveWorkoutScreen(
    uiState: WearActiveWorkoutUiState,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    onIncrementWgt: () -> Unit,
    onDecrementWgt: () -> Unit,
    modifier: Modifier = Modifier,
    onCompleteSet: (() -> Unit)? = null,
    onStartNextExercise: (() -> Unit)? = null,
    onTogglePause: (() -> Unit)? = null,
    onNextExercise: (() -> Unit)? = null,
    onPreviousExercise: (() -> Unit)? = null,
) {
    val exerciseSession =
        when (val state = uiState.trainingStepState) {
            is TrainingStepState.Active -> state.exerciseSession
            is TrainingStepState.Cooldown -> state.exerciseSession
            is TrainingStepState.ReadyForNext -> state.nextExerciseSession
        }

    val currentSet =
        when (val state = uiState.trainingStepState) {
            is TrainingStepState.Active -> state.currentSet
            else -> exerciseSession?.completedSets ?: 0
        }

    val exerciseTitle =
        exerciseSession?.name
            ?: uiState.exerciseName.ifEmpty { stringResource(R.string.wear_default_exercise) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundDark),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            WorkoutHeaderRow(
                workoutDurationSeconds = uiState.workoutDurationSeconds,
                currentHeartRateBpm = uiState.currentHeartRateBpm,
            )

            Text(
                text = exerciseTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )

            SetAndRepsContent(
                uiState = uiState,
                targetSets = exerciseSession?.targetSets ?: 0,
                targetReps = exerciseSession?.targetReps ?: "0",
                currentSet = currentSet,
                onIncrementReps = onIncrementReps,
                onDecrementReps = onDecrementReps,
                onIncrementWgt = onIncrementWgt,
                onDecrementWgt = onDecrementWgt,
            )

            WorkoutActionButton(
                isReadyForNext = uiState.trainingStepState is TrainingStepState.ReadyForNext,
                onStartNextExercise = onStartNextExercise,
                onCompleteSet = onCompleteSet,
            )

            WorkoutBottomControlRow(
                isPaused = uiState.isPaused,
                onTogglePause = onTogglePause,
                onNextExercise = onNextExercise,
                onPreviousExercise = onPreviousExercise,
            )
        }
    }
}

@Composable
private fun WorkoutHeaderRow(
    workoutDurationSeconds: Long,
    currentHeartRateBpm: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatDuration(workoutDurationSeconds),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryCyan,
            modifier = Modifier.padding(start = 45.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 45.dp),
        ) {
            Text(
                text = if (currentHeartRateBpm > 0) "$currentHeartRateBpm" else "--",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorContainerDark,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.wear_bpm_unit),
                fontSize = 9.sp,
                color = OnSurfaceDark,
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun SetAndRepsContent(
    uiState: WearActiveWorkoutUiState,
    targetSets: Int,
    targetReps: String,
    currentSet: Int,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    onIncrementWgt: () -> Unit,
    onDecrementWgt: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (targetSets > 0) {
            Text(
                text = stringResource(R.string.set_of_target, currentSet, targetSets, targetReps),
                fontSize = 11.sp,
                color = OnSurfaceDark,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Row {
            FieldIntValueComponent(
                R.string.reps,
                "${uiState.currentReps}",
                onDecrementReps,
                onIncrementReps
            )

            Spacer(modifier = Modifier.height(2.dp))

            FieldIntValueComponent(
                R.string.weight,
                "${uiState.currentWeight}",
                onDecrementWgt,
                onIncrementWgt
            )
        }
    }
}

@Composable
private fun RowScope.FieldIntValueComponent(@StringRes name:Int, value:String, onDecrementReps:()->Unit, onIncrementReps: () -> Unit){
    Column(modifier=Modifier.weight(1f)) {
        Text(
            text=stringResource(name),
            textAlign = TextAlign.Center,
            modifier=Modifier.fillMaxWidth())
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = onDecrementReps,
                modifier = Modifier.size(28.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = OnSurfaceDark,
                    ),
                shape = CircleShape,
            ) {
                Text(
                    text = stringResource(R.string.wear_dec_reps),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryCyan,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onIncrementReps,
                modifier = Modifier.size(28.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = PrimaryCyan,
                    ),
                shape = CircleShape,
            ) {
                Text(
                    text = stringResource(R.string.wear_inc_reps),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun WorkoutActionButton(
    isReadyForNext: Boolean,
    onStartNextExercise: (() -> Unit)?,
    onCompleteSet: (() -> Unit)?,
) {
    val buttonText = if (isReadyForNext) R.string.next_exercise  else R.string.completed_set
    val onClickAction = if (isReadyForNext) onStartNextExercise else onCompleteSet

    Chip(
        onClick = { onClickAction?.invoke() },
        colors =
            ChipDefaults.chipColors(
                backgroundColor = PrimaryCyan,
                contentColor = OnPrimaryDark,
            ),
        label = {
            Text(
                text = stringResource(buttonText),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        modifier = Modifier
            .fillMaxWidth(CHIP_WIDTH_FRACTION)
            .height(32.dp),
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
private fun WorkoutBottomControlRow(
    isPaused: Boolean,
    onTogglePause: (() -> Unit)?,
    onNextExercise: (() -> Unit)?,
    onPreviousExercise: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp, end=20.dp, start = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = { onPreviousExercise?.invoke() },
            modifier = Modifier.size(26.dp),
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceContainerLow,
                    contentColor = OnSurfaceDark,
                ),
            shape = CircleShape,
        ) {
            Text(text = "‹", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onTogglePause?.invoke() },
            modifier = Modifier.size(26.dp),
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = if (isPaused) ErrorContainerDark else SurfaceContainerHigh,
                    contentColor = if (isPaused) PrimaryCyan else OnSurfaceDark,
                ),
            shape = CircleShape,
        ) {
            Text(
                text = if (isPaused) "▶" else "❚❚",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(
            onClick = { onNextExercise?.invoke() },
            modifier = Modifier.size(26.dp),
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceContainerLow,
                    contentColor = OnSurfaceDark,
                ),
            shape = CircleShape,
        ) {
            Text(text = "›", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hrs = seconds / SECONDS_PER_HOUR
    val mins = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val secs = seconds % SECONDS_PER_MINUTE
    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}

@Preview(device =  Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
private fun WearActiveWorkoutScreenPreview1(){
    val uiState =  WearActiveWorkoutUiState()
    MaterialThemeComposeUI {
        WearActiveWorkoutScreen(
            uiState = uiState,
            onIncrementReps = {},
            onDecrementReps = {  },
            onIncrementWgt = {},
            onDecrementWgt = {  },
            modifier = Modifier,
            onCompleteSet = {},
            onStartNextExercise = {},
            onTogglePause = {},
            onNextExercise = {},
            onPreviousExercise = {},
        )
    }
}

@Preview(device =  Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
private fun WearActiveWorkoutScreenPreview2(){
    val vm =  WearActiveWorkoutViewModel()
    val uiState by vm.uiState.collectAsState()

    MaterialThemeComposeUI {
        WearActiveWorkoutScreen(
            uiState = uiState,
            onIncrementReps = {},
            onDecrementReps = {  },
            onIncrementWgt = {},
            onDecrementWgt = {  },
            modifier = Modifier,
            onCompleteSet = {},
            onStartNextExercise = {},
            onTogglePause = {},
            onNextExercise = {},
            onPreviousExercise = {},
        )
    }
}
