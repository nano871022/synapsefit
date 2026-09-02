@file:Suppress(
    "FunctionNaming",
    "LongMethod",
    "LongParameterList",
    "MagicNumber",
    "MaxLineLength",
    "ImplicitDefaultLocale",
    "CyclomaticComplexMethod",
    "UnusedPrivateMember",
)

package co.japl.android.synapsefit.app.ui.workout

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.HeartRateGauge
import co.japl.android.synapsefit.ui.components.KineticCard
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme
import co.japl.android.synapsefit.ui.theme.spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun ActiveWorkoutSessionScreen(
    state: ActiveWorkoutUiState,
    onSetRepsChange: (setIndex: Int, reps: String) -> Unit,
    onSetWeightChange: (setIndex: Int, weight: String) -> Unit,
    onCompleteSet: (setIndex: Int) -> Unit,
    onNextSetOrExercise: () -> Unit = {},
    onFinishSession: () -> Unit,
    onOpenImagePopup: () -> Unit = {},
    onCloseImagePopup: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

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

            val exerciseIndexDisplay = if (state.exercises.isNotEmpty()) state.currentExerciseIndex + 1 else 1
            val totalExercisesDisplay = if (state.exercises.isNotEmpty()) state.exercises.size else 1

            ExerciseHeader(
                currentExerciseIndex = exerciseIndexDisplay,
                totalExercises = totalExercisesDisplay,
                exerciseName = state.currentExerciseName,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                val queryFormatted = state.currentExerciseName.replace(" ", "+")
                val videoUrl = state.exerciseVideoUrl ?: "https://www.youtube.com/results?search_query=$queryFormatted"
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.watch_video))
                }

                OutlinedButton(
                    onClick = onOpenImagePopup,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.view_image))
                }
            }

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

                    val isResting = state.restTimerSecondsRemaining != null

                    OutlinedTextField(
                        value = state.currentSetWeightKg,
                        onValueChange = { onSetWeightChange(state.currentSetIndex, it) },
                        label = { Text(stringResource(R.string.weight_col)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !state.isCurrentSetCompleted && !isResting,
                    )

                    OutlinedTextField(
                        value = state.currentSetReps,
                        onValueChange = { onSetRepsChange(state.currentSetIndex, it) },
                        label = { Text(stringResource(R.string.reps_col)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !state.isCurrentSetCompleted && !isResting,
                    )

                    if (!state.isCurrentSetCompleted) {
                        val restText = "En Descanso (${state.restTimerSecondsRemaining}s)"
                        NeonButton(
                            text = if (isResting) restText else stringResource(R.string.complete_set),
                            onClick = { onCompleteSet(state.currentSetIndex) },
                            enabled = state.currentSetReps.isNotBlank() && !isResting,
                        )
                    } else {
                        NeonButton(
                            text = stringResource(R.string.next_set_or_exercise),
                            onClick = onNextSetOrExercise,
                            enabled = !isResting,
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

    if (state.isImagePopupVisible && state.exerciseImageUrl != null) {
        ExerciseImageDialog(
            imageUrl = state.exerciseImageUrl,
            onDismiss = onCloseImagePopup,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveWorkoutSessionScreenPreview() {
    SynapseFitTheme {
        ActiveWorkoutSessionScreen(
            state =
                ActiveWorkoutUiState(
                    planTitle = "Sesión Activa - Día 1",
                    currentExerciseName = "Press de Banca (Barra)",
                    currentSetIndex = 1,
                    totalSetsForCurrentExercise = 4,
                    targetRepsForCurrentSet = "10-12",
                    currentSetReps = "10",
                    currentSetWeightKg = "60.0",
                    elapsedTimeSeconds = 300,
                    heartRateBpm = 135,
                ),
            onSetRepsChange = { _, _ -> },
            onSetWeightChange = { _, _ -> },
            onCompleteSet = {},
            onFinishSession = {},
        )
    }
}

@Composable
fun ExerciseHeader(
    currentExerciseIndex: Int,
    totalExercises: Int,
    exerciseName: String,
    modifier: Modifier = Modifier,
) {
    val rawName = exerciseName.trim()
    val hasParentheses = rawName.contains("(") && rawName.contains(")")
    val title = if (hasParentheses) rawName.substringBefore("(").trim() else rawName
    val description = if (hasParentheses) rawName.substringAfter("(").substringBefore(")").trim() else ""

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
            Text(
                text = "EJERCICIO $currentExerciseIndex DE $totalExercises".uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title.ifBlank { stringResource(R.string.app_name) },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ExerciseImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fallbackUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd"
    val targetUrl = imageUrl.ifBlank { fallbackUrl }
    var imageBitmap by remember(targetUrl) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var isLoading by remember(targetUrl) { mutableStateOf(true) }

    LaunchedEffect(targetUrl) {
        isLoading = true
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = URL(targetUrl).openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.asImageBitmap()
            }.getOrNull() ?: runCatching {
                if (targetUrl != fallbackUrl) {
                    val connection = URL(fallbackUrl).openConnection()
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    val inputStream = connection.getInputStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.asImageBitmap()
                } else {
                    null
                }
            }.getOrNull()
        }.also { bitmap ->
            imageBitmap = bitmap
            isLoading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text = stringResource(R.string.view_image),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.height(64.dp).width(64.dp),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                NeonButton(
                    text = stringResource(R.string.close),
                    onClick = onDismiss,
                )
            }
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
