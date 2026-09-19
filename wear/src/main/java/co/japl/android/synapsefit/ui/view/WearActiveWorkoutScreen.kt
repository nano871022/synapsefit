@file:Suppress("TooManyFunctions")

package co.japl.android.synapsefit.ui.view

import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.ErrorContainerDark
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainer
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.com.japl.ui.theme.SurfaceContainerLow
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.viewmodel.FocusedInput
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutUiState
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import java.util.Locale

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3600
private const val CHIP_WIDTH_FRACTION = 0.85f
private const val LIVE_BLINK_DURATION_MS = 800

@Suppress("LongMethod", "LongParameterList", "CyclomaticComplexMethod")
@Composable
fun WearActiveWorkoutScreen(
    uiState: WearActiveWorkoutUiState,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    onIncrementWgt: () -> Unit,
    onDecrementWgt: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectFocus: ((FocusedInput) -> Unit)? = null,
    onRotaryScroll: ((Float) -> Unit)? = null,
    onHardwareKey: ((Int) -> Boolean)? = null,
    onOpenNumericKeypad: (() -> Unit)? = null,
    onCloseNumericKeypad: (() -> Unit)? = null,
    onDirectValueEntered: ((Int) -> Unit)? = null,
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
            is TrainingStepState.Paused -> {
                when (val prev = state.previousState) {
                    is TrainingStepState.Active -> prev.exerciseSession
                    is TrainingStepState.Cooldown -> prev.exerciseSession
                    is TrainingStepState.ReadyForNext -> prev.nextExerciseSession
                    else -> null
                }
            }
        }

    val currentSet =
        when (val state = uiState.trainingStepState) {
            is TrainingStepState.Active -> state.currentSet
            else -> exerciseSession?.completedSets ?: 0
        }

    val exerciseTitle =
        exerciseSession?.name
            ?: uiState.exerciseName.ifEmpty { stringResource(R.string.wear_default_exercise) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .focusRequester(focusRequester)
                .focusable()
                .onRotaryScrollEvent { event ->
                    onRotaryScroll?.invoke(event.verticalScrollPixels)
                    true
                }
                .onKeyEvent { keyEvent ->
                    onHardwareKey?.invoke(keyEvent.nativeKeyEvent.keyCode) ?: false
                },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            WorkoutHeaderRow(
                workoutDurationSeconds = uiState.workoutDurationSeconds,
                currentHeartRateBpm = uiState.currentHeartRateBpm,
                isLiveSyncActive = uiState.isLiveSyncActive,
            )

            Text(
                text = exerciseTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
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
                onSelectFocus = onSelectFocus,
                onOpenNumericKeypad = onOpenNumericKeypad,
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

        if (uiState.isNumericKeypadOpen) {
            val keypadTitle =
                if (uiState.focusedInput == FocusedInput.REPS) {
                    stringResource(R.string.reps)
                } else {
                    stringResource(R.string.weight)
                }
            val keypadVal =
                if (uiState.focusedInput == FocusedInput.REPS) {
                    uiState.currentReps
                } else {
                    uiState.currentWeight.toInt()
                }

            NumericKeypadDialog(
                title = keypadTitle,
                initialValue = keypadVal,
                onDismiss = { onCloseNumericKeypad?.invoke() },
                onConfirm = { numericValue -> onDirectValueEntered?.invoke(numericValue) },
            )
        }
    }
}

@Composable
private fun WorkoutHeaderRow(
    workoutDurationSeconds: Long,
    currentHeartRateBpm: Int,
    isLiveSyncActive: Boolean = false,
) {
    Row(
        modifier =
            Modifier
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

        if (isLiveSyncActive) {
            val infiniteTransition = rememberInfiniteTransition(label = "wear_live_blink")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.2f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(LIVE_BLINK_DURATION_MS),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "wear_alpha_blink",
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(6.dp)
                            .alpha(alpha)
                            .background(Color.Red, CircleShape),
                )
                Text(
                    text = "LIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                )
            }
        }

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
    onSelectFocus: ((FocusedInput) -> Unit)?,
    onOpenNumericKeypad: (() -> Unit)?,
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
                name = R.string.reps,
                value = "${uiState.currentReps}",
                isFocused = uiState.focusedInput == FocusedInput.REPS,
                onSelectFocus = { onSelectFocus?.invoke(FocusedInput.REPS) },
                onDecrement = onDecrementReps,
                onIncrement = onIncrementReps,
                onOpenKeypad = {
                    onSelectFocus?.invoke(FocusedInput.REPS)
                    onOpenNumericKeypad?.invoke()
                },
            )

            Spacer(modifier = Modifier.width(4.dp))

            FieldIntValueComponent(
                name = R.string.weight,
                value = "${uiState.currentWeight}",
                isFocused = uiState.focusedInput == FocusedInput.WEIGHT,
                onSelectFocus = { onSelectFocus?.invoke(FocusedInput.WEIGHT) },
                onDecrement = onDecrementWgt,
                onIncrement = onIncrementWgt,
                onOpenKeypad = {
                    onSelectFocus?.invoke(FocusedInput.WEIGHT)
                    onOpenNumericKeypad?.invoke()
                },
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun RowScope.FieldIntValueComponent(
    @StringRes name: Int,
    value: String,
    isFocused: Boolean,
    onSelectFocus: () -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onOpenKeypad: () -> Unit,
) {
    val focusBorderModifier =
        if (isFocused) {
            Modifier.border(1.5.dp, PrimaryCyan, RoundedCornerShape(8.dp))
        } else {
            Modifier.border(1.dp, Color.Transparent, RoundedCornerShape(8.dp))
        }

    Column(
        modifier =
            Modifier
                .weight(1f)
                .clickable { onSelectFocus() }
                .then(focusBorderModifier)
                .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(name),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = if (isFocused) PrimaryCyan else OnSurfaceDark,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = if (isFocused) PrimaryCyan else OnSurfaceDark.copy(alpha = 0.5f),
                modifier =
                    Modifier
                        .size(10.dp)
                        .clickable { onOpenKeypad() },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = onDecrement,
                modifier = Modifier.size(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = OnSurfaceDark,
                    ),
                shape = CircleShape,
            ) {
                Text(
                    text = stringResource(R.string.wear_dec_reps),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isFocused) PrimaryCyan else OnSurfaceDark,
            )

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = onIncrement,
                modifier = Modifier.size(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = PrimaryCyan,
                    ),
                shape = CircleShape,
            ) {
                Text(
                    text = stringResource(R.string.wear_inc_reps),
                    fontSize = 12.sp,
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
    val buttonText = if (isReadyForNext) R.string.next_exercise else R.string.completed_set
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
        modifier =
            Modifier
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
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp, end = 20.dp, start = 20.dp),
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

@Composable
private fun NumericKeypadDialog(
    title: String,
    initialValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var inputString by remember { mutableStateOf(if (initialValue > 0) "$initialValue" else "") }

    Dialog(
        showDialog = true,
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan,
                )
                Text(
                    text = inputString.ifEmpty { "0" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceDark,
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("1", "2", "3").forEach { num ->
                        KeypadButton(num) { inputString += num }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("4", "5", "6").forEach { num ->
                        KeypadButton(num) { inputString += num }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("7", "8", "9").forEach { num ->
                        KeypadButton(num) { inputString += num }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("C") { inputString = "" }
                    KeypadButton("0") { inputString += "0" }
                    Button(
                        onClick = {
                            val parsed = inputString.toIntOrNull() ?: 0
                            onConfirm(parsed)
                        },
                        modifier = Modifier.size(28.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                backgroundColor = PrimaryCyan,
                                contentColor = OnPrimaryDark,
                            ),
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "OK",
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(28.dp),
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = SurfaceContainer,
                contentColor = OnSurfaceDark,
            ),
        shape = CircleShape,
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearActiveWorkoutScreenPreview1() {
    val uiState = WearActiveWorkoutUiState()
    MaterialThemeComposeUI {
        WearActiveWorkoutScreen(
            uiState = uiState,
            onIncrementReps = {},
            onDecrementReps = { },
            onIncrementWgt = {},
            onDecrementWgt = { },
            modifier = Modifier,
            onCompleteSet = {},
            onStartNextExercise = {},
            onTogglePause = {},
            onNextExercise = {},
            onPreviousExercise = {},
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearActiveWorkoutScreenPreview2() {
    val vm = WearActiveWorkoutViewModel()
    val uiState by vm.uiState.collectAsState()

    MaterialThemeComposeUI {
        WearActiveWorkoutScreen(
            uiState = uiState,
            onIncrementReps = {},
            onDecrementReps = { },
            onIncrementWgt = {},
            onDecrementWgt = { },
            modifier = Modifier,
            onCompleteSet = {},
            onStartNextExercise = {},
            onTogglePause = {},
            onNextExercise = {},
            onPreviousExercise = {},
        )
    }
}
