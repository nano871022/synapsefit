package co.japl.android.synapsefit.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.WEAR_OS_SMALL_ROUND
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainer
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Suppress("LongMethod", "LongParameterList")
@Composable
fun WearPreWorkoutSelectionHubScreen(
    planTitle: String,
    currentDay: Int,
    exercises: List<Exercise>,
    onSelectExercise: (Exercise, Int) -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(R.string.wear_workout_hub),
                    style = MaterialTheme.typography.caption1,
                    color = OnSurfaceDark.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }

            item {
                HeaderCard(
                    currentDay = currentDay,
                    planTitle = planTitle,
                    exerciseCount = exercises.size,
                    onClick=onStartSession
                )
            }

            if (exercises.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.wear_no_exercises),
                        style = MaterialTheme.typography.body2,
                        color = OnSurfaceDark,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                itemsIndexed(exercises) { index, exercise ->
                    ExerciseCardItem(
                        exercise = exercise,
                        onClick = { onSelectExercise(exercise, index) },
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.wear_tap_to_start_exercise),
                        fontSize = 9.sp,
                        color = OnSurfaceDark.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(
    currentDay: Int,
    planTitle: String,
    exerciseCount: Int,
    onClick: ()->Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainer)
                .padding(12.dp)
                .clickable(enabled=true,onClickLabel=">"){
                    onClick.invoke()
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text =
                    stringResource(
                        R.string.wear_day_header,
                        currentDay,
                        planTitle.ifEmpty { stringResource(R.string.wear_default_exercise) },
                    ),
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.wear_programmed_exercises, exerciseCount),
            style = MaterialTheme.typography.caption2,
            color = OnSurfaceDark.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun ExerciseCardItem(
    exercise: Exercise,
    onClick: () -> Unit,
) {
    val (title, detail) = parseExerciseName(exercise.name)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerHigh)
                .clickable(onClick = onClick)
                .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (detail.isNotEmpty()) {
                    Text(
                        text = detail,
                        fontSize = 10.sp,
                        color = PrimaryCyan.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text =
                        stringResource(
                            R.string.wear_exercise_sets_reps_timer,
                            exercise.targetSets,
                            exercise.targetReps,
                            exercise.restSeconds,
                        ),
                    fontSize = 10.sp,
                    color = OnSurfaceDark.copy(alpha = 0.7f),
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun parseExerciseName(fullName: String): Pair<String, String> {
    val regex = Regex("""^(.+?)\s*\((.+)\)$""")
    val match = regex.matchEntire(fullName.trim())
    return if (match != null) {
        Pair(match.groupValues[1].trim(), match.groupValues[2].trim())
    } else {
        Pair(fullName.trim(), "")
    }
}

@Composable
@Preview(device = WEAR_OS_SMALL_ROUND, showSystemUi = true)
private fun WearPreWorkoutSelectionHubScreenPreview(){
    val list = listExercises()
    MaterialThemeComposeUI {
        WearPreWorkoutSelectionHubScreen(
            planTitle = "Plan 1",
            currentDay = 1,
            exercises = list,
            onSelectExercise = { exer, pos -> },
            onStartSession = {  },
            modifier = Modifier,
        )
    }
}

@Composable
private fun listExercises():List<Exercise>{
    return listOf(
        Exercise(
            id = "1",
            planId = "1",
            name = "Exercise 1",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            guideVideoUrl = "",
            guideImageUrl = "",
            createdAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ),
        Exercise(
            id = "2",
            planId = "1",
            name = "Exercise 2",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            guideVideoUrl = "",
            guideImageUrl = "",
            createdAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ),
        Exercise(
            id = "3",
            planId = "1",
            name = "Exercise 3",
            muscleGroup = "Arms",
            targetSets = 4,
            targetReps = "10-15",
            restSeconds = 90,
            day = 1,
            guideVideoUrl = "",
            guideImageUrl = "",
            createdAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        )

    )
}
