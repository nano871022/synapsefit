package co.japl.android.synapsefit.ui.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.ErrorContainerDark
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.util.rememberRotaryScrollAdapter
import co.japl.android.synapsefit.ui.util.rotaryScrollable
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel

private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_SECOND = 1000L
private const val COMPLETED_ALPHA = 0.4f

@Suppress("LongParameterList", "LongMethod")
@Composable
fun WearCooldownTransitionScreen(
    trainingStepState: TrainingStepState,
    exerciseSessions: List<ExerciseSession>,
    heartRateBpm: Int,
    onAddExtraTime: () -> Unit,
    onSkipRest: () -> Unit,
    onSelectExercise: (ExerciseSession, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    val rotaryAdapter = rememberRotaryScrollAdapter(listState)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundDark),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    ) {
        ScalingLazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .rotaryScrollable(focusRequester, rotaryAdapter),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                HeaderCooldownSection(
                    trainingStepState = trainingStepState,
                    heartRateBpm = heartRateBpm,
                    onAddExtraTime = onAddExtraTime,
                    onSkipRest = onSkipRest,
                )
            }

            itemsIndexed(exerciseSessions) { index, session ->
                val isCurrentSession =
                    when (trainingStepState) {
                        is TrainingStepState.Active ->
                            trainingStepState.exerciseSession.exerciseId == session.exerciseId
                        is TrainingStepState.Cooldown ->
                            trainingStepState.exerciseSession.exerciseId == session.exerciseId
                        is TrainingStepState.ReadyForNext ->
                            trainingStepState.nextExerciseSession?.exerciseId == session.exerciseId
                        is TrainingStepState.Paused -> {
                            val prev = trainingStepState.previousState
                            when (prev) {
                                is TrainingStepState.Active ->
                                    prev.exerciseSession.exerciseId == session.exerciseId
                                is TrainingStepState.Cooldown ->
                                    prev.exerciseSession.exerciseId == session.exerciseId
                                is TrainingStepState.ReadyForNext ->
                                    prev.nextExerciseSession?.exerciseId == session.exerciseId
                                is TrainingStepState.Paused -> false
                            }
                        }
                    }

                ExerciseSessionCardItem(
                    session = session,
                    isCurrentSession = isCurrentSession,
                    trainingStepState = trainingStepState,
                    onClick = {
                        if (!session.isCompleted) {
                            onSelectExercise(session, index)
                        }
                    },
                )
            }

            item {
                FooterIndexSection(
                    exerciseSessions = exerciseSessions,
                    trainingStepState = trainingStepState,
                )
            }
        }
    }
}

@Composable
private fun HeaderCooldownSection(
    trainingStepState: TrainingStepState,
    heartRateBpm: Int,
    onAddExtraTime: () -> Unit,
    onSkipRest: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 8.dp, top = 25.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = ErrorContainerDark,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (heartRateBpm > 0) "$heartRateBpm" else "--",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorContainerDark,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.wear_bpm_unit),
                fontSize = 20.sp,
                color = OnSurfaceDark,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.wear_rest_timer),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryCyan,
            )
        }

        Timer(trainingStepState)

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onSkipRest,
                modifier = Modifier.size(40.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = PrimaryCyan,
                    ),
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = stringResource(R.string.wear_skip_rest),
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onAddExtraTime,
                modifier = Modifier.size(40.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = SurfaceContainerHigh,
                        contentColor = PrimaryCyan,
                    ),
                shape = CircleShape,
            ) {
                Text(
                    text = stringResource(R.string.wear_add_30s),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun Timer(
    trainingStepState: TrainingStepState,
    fontSize: TextUnit = 30.sp,
    color: Color = PrimaryCyan,
    ){
    val remainingSeconds =
        when (trainingStepState) {
            is TrainingStepState.Cooldown ->
                (trainingStepState.remainingMillis / MILLIS_PER_SECOND).toInt()
            else -> 0
        }
    val mins = remainingSeconds / SECONDS_PER_MINUTE
    val secs = remainingSeconds % SECONDS_PER_MINUTE

    Text(
        text = "%1$02d:%2$02d".format(mins, secs),
        fontSize = fontSize,
        fontWeight = FontWeight.ExtraBold,
        color = color,
    )
}

@Suppress("LongMethod")
@Composable
private fun ExerciseSessionCardItem(
    session: ExerciseSession,
    isCurrentSession: Boolean,
    trainingStepState: TrainingStepState,
    onClick: () -> Unit,
) {
    if (session.isCompleted) {
        SessionCompleted(
            session = session,
            onClick = onClick,
        )
    } else if (isCurrentSession) {
        CurrentSession(
            session = session,
            trainingStepState = trainingStepState,
            onClick = onClick,
        )
    } else {
        SessionCard(
            session = session,
            onClick = onClick,
        )
    }
}

@Composable
private fun SessionCard(
    session: ExerciseSession,
    onClick: () -> Unit,
) {
    TitleCard(
        onClick = onClick,
        title = {
            Text(
                text = session.name,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark,
                softWrap = true,
            )
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
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text =
                stringResource(
                    R.string.wear_exercise_sets_reps_timer,
                    session.targetSets,
                    session.targetReps,
                    session.restSeconds,
                ),
            style = MaterialTheme.typography.caption1,
            color = OnSurfaceDark.copy(alpha = 0.8f),
        )
    }
}

@Suppress("UnusedParameter")
@Composable
private fun CurrentSession(
    session: ExerciseSession,
    trainingStepState: TrainingStepState,
    onClick: () -> Unit,
) {
    val timeSlot: @Composable () -> Unit = {
        val currentSet = (session.completedSets + 1).coerceAtMost(session.targetSets)
        Text(
            text =
                stringResource(
                    R.string.wear_next_set_format,
                    currentSet,
                    session.targetSets,
                ),
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = OnPrimaryDark,
        )
    }

    TitleCard(
        onClick = onClick,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = OnPrimaryDark,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.name,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                timeSlot()
            }
        },
        backgroundPainter =
            CardDefaults.cardBackgroundPainter(
                startBackgroundColor = PrimaryCyan,
                endBackgroundColor = PrimaryCyan,
            ),
        contentColor = OnPrimaryDark,
        titleColor = OnPrimaryDark,
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(top = 2.dp)) {

            Timer(trainingStepState)

            Text(
                text =
                    stringResource(
                        R.string.wear_exercise_sets_reps_timer,
                        session.targetSets,
                        session.targetReps,
                        session.restSeconds,
                    ),
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.SemiBold,
                color = OnPrimaryDark,
            )
        }
    }
}

@Composable
private fun SessionCompleted(
    session: ExerciseSession,
    onClick: () -> Unit,
) {
    TitleCard(
        onClick = onClick,
        enabled = false,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = OnSurfaceDark.copy(alpha = COMPLETED_ALPHA),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = session.name,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark.copy(alpha = COMPLETED_ALPHA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        backgroundPainter =
            CardDefaults.cardBackgroundPainter(
                startBackgroundColor = SurfaceContainerHigh.copy(alpha = COMPLETED_ALPHA),
                endBackgroundColor = SurfaceContainerHigh.copy(alpha = COMPLETED_ALPHA),
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .alpha(COMPLETED_ALPHA),
    ) {
        Column {
            Text(
                text = stringResource(R.string.wear_completed_badge),
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan.copy(alpha = COMPLETED_ALPHA),
            )
            Text(
                text =
                    stringResource(
                        R.string.wear_sets_ready_format,
                        session.completedSets,
                        session.targetSets,
                        session.restSeconds,
                    ),
                style = MaterialTheme.typography.caption2,
                color = OnSurfaceDark.copy(alpha = COMPLETED_ALPHA),
            )
            Text(
                text = stringResource(R.string.weight_avg, session.weightAvg),
                style = MaterialTheme.typography.caption2,
                color = OnSurfaceDark.copy(alpha = COMPLETED_ALPHA),
            )
        }
    }
}

@Composable
private fun FooterIndexSection(
    exerciseSessions: List<ExerciseSession>,
    trainingStepState: TrainingStepState,
) {
    if (exerciseSessions.isEmpty()) return

    val currentIndex =
        when (trainingStepState) {
            is TrainingStepState.Active -> {
                val idx =
                    exerciseSessions.indexOfFirst {
                        it.exerciseId == trainingStepState.exerciseSession.exerciseId
                    }
                if (idx >= 0) idx + 1 else 1
            }
            is TrainingStepState.Cooldown -> {
                val idx =
                    exerciseSessions.indexOfFirst {
                        it.exerciseId == trainingStepState.exerciseSession.exerciseId
                    }
                if (idx >= 0) idx + 1 else 1
            }
            is TrainingStepState.ReadyForNext -> {
                val nextSession = trainingStepState.nextExerciseSession
                if (nextSession != null) {
                    val idx =
                        exerciseSessions.indexOfFirst {
                            it.exerciseId == nextSession.exerciseId
                        }
                    if (idx >= 0) idx + 1 else 1
                } else {
                    exerciseSessions.size
                }
            }
            is TrainingStepState.Paused -> 1
        }

    Box(
        modifier =
            Modifier
                .padding(top = 8.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerHigh)
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text =
                stringResource(
                    R.string.wear_exercise_index_footer,
                    currentIndex,
                    exerciseSessions.size,
                ),
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = PrimaryCyan,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearCooldownTransitionScreenPreview() {
    val state = WearActiveWorkoutViewModel()
    val activeUiState by state.uiState.collectAsState()
    val exerciseSession = listExercises()
    MaterialThemeComposeUI {
        WearCooldownTransitionScreen(
            trainingStepState = activeUiState.trainingStepState,
            exerciseSessions = exerciseSession,
            heartRateBpm = 20,
            onAddExtraTime = {},
            onSkipRest = { },
            onSelectExercise = { exe, id -> },
            modifier = Modifier,
        )
    }
}

@Composable
private fun listExercises(): List<ExerciseSession> {
    return listOf(
        ExerciseSession(
            exerciseId = "1",
            planId = "1",
            name = "Exercise 1",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            completedSets = 4,
            isCompleted = true,
        ),
        ExerciseSession(
            exerciseId = "2",
            planId = "1",
            name = "Exercise 2",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            completedSets = 2,
            isCompleted = false,
        ),
        ExerciseSession(
            exerciseId = "3",
            planId = "1",
            name = "Exercise 3",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            completedSets = 0,
            isCompleted = false,
        ),
    )
}
