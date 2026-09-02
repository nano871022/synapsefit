@file:Suppress("FunctionNaming", "LongMethod", "MaxLineLength", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.workout.ExerciseUiModel
import co.japl.android.synapsefit.app.controller.workout.WorkoutPlanDetailUiState
import co.japl.android.synapsefit.ui.components.NeonButton

@Composable
fun WorkoutPlanDetailScreen(
    state: WorkoutPlanDetailUiState,
    onStartSessionClick: (planId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedExercises =
        state.exercises.groupBy { exercise ->
            "Día ${exercise.day}"
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (state.planId.isNotBlank()) {
                NeonButton(
                    text = stringResource(R.string.start_workout),
                    onClick = { onStartSessionClick(state.planId) },
                    modifier = Modifier.padding(MaterialTheme.spacing.marginEdge),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(MaterialTheme.spacing.marginEdge),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            item {
                Text(
                    text = state.planTitle.ifBlank { stringResource(R.string.plan_detail) },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                RoutineSummaryModule(
                    goalDescription = state.goalDescription,
                    totalExercises = state.totalExercises,
                )
            }

            item {
                Text(
                    text = stringResource(R.string.prescribed_exercises),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            groupedExercises.forEach { (dayLabel, exercisesForDay) ->
                item {
                    DayExerciseCard(
                        dayLabel = dayLabel,
                        exercises = exercisesForDay,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutPlanDetailScreenPreview() {
    MaterialThemeComposeUI {
        WorkoutPlanDetailScreen(
            state =
                WorkoutPlanDetailUiState(
                    planId = "plan_1",
                    planTitle = "Rutina Hipertrofia",
                    goalDescription = "4 Días por semana",
                    totalExercises = 4,
                    exercises =
                        listOf(
                            ExerciseUiModel(
                                id = "ex_1",
                                name = "Press de Banca (Barra)",
                                muscleGroup = "Pecho",
                                targetSets = 4,
                                targetReps = "10-12",
                                restSeconds = 90,
                                day = 1,
                            ),
                        ),
                ),
            onStartSessionClick = {},
        )
    }
}

@Composable
fun DayExerciseCard(
    dayLabel: String,
    exercises: List<ExerciseUiModel>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = dayLabel.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            exercises.forEach { exercise ->
                ExerciseListItem(exercise = exercise)
            }
        }
    }
}

@Composable
fun RoutineSummaryModule(
    goalDescription: String,
    totalExercises: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EJERCICIOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "$totalExercises",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.objective).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = goalDescription.ifBlank { stringResource(R.string.no_description_available) },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun ExerciseListItem(
    exercise: ExerciseUiModel,
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val rawName = exercise.name.trim()
                val hasParentheses = rawName.contains("(") && rawName.contains(")")
                val title = if (hasParentheses) rawName.substringBefore("(").trim() else rawName
                val description = if (hasParentheses) rawName.substringAfter("(").substringBefore(")").trim() else ""

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { },
                        label = { Text(exercise.muscleGroup, style = MaterialTheme.typography.labelSmall) },
                    )
                    Text(
                        text = "${exercise.targetSets} x ${exercise.targetReps}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
            Text(
                text = stringResource(R.string.rest_seconds_abbr, exercise.restSeconds),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
