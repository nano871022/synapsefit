@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun AICoachGeneratorScreen(
    state: AICoachGeneratorUiState,
    onEnvironmentSelected: (TrainingEnvironment) -> Unit,
    onGymChainQueryChange: (String) -> Unit,
    onPromptContextChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.marginEdge)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.ai_coach_generator),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        state.generationError?.let { err ->
            Text(
                text = err,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Text(
            text = stringResource(R.string.training_environment),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        EnvironmentSelector(
            selectedEnvironment = state.selectedEnvironment,
            onEnvironmentSelected = onEnvironmentSelected,
        )

        if (state.selectedEnvironment == TrainingEnvironment.CHAIN_GYM) {
            GymChainSearchInput(
                query = state.gymChainQuery,
                onQueryChange = onGymChainQueryChange,
            )
        }

        Text(
            text = stringResource(R.string.goal_and_context),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        OutlinedTextField(
            value = state.promptContext,
            onValueChange = onPromptContextChange,
            label = { Text(stringResource(R.string.goal_prompt_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        NeonButton(
            text = if (state.generatedPlan == null) stringResource(R.string.generate_plan_ai) else "Generar Otra",
            onClick = onGenerateClick,
            isLoading = state.isGenerating,
            enabled = !state.isGenerating,
        )

        if (state.generatedPlan != null) {
            PlanPreview(
                plan = state.generatedPlan,
                exercises = state.generatedExercises,
                onAccept = onAcceptClick,
                onDiscard = onDiscardClick,
                isLoading = state.isLoading
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Tu rutina personalizada aparecerá aquí...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun PlanPreview(
    plan: WorkoutPlan,
    exercises: List<Exercise>,
    onAccept: () -> Unit,
    onDiscard: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = plan.goalDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Ejercicios:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            exercises.forEach { exercise ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${exercise.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${exercise.targetSets}x${exercise.targetReps}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Músculo: ${exercise.muscleGroup} | Descanso: ${exercise.restSeconds}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonButton(
                    text = "Aceptar",
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                )
                NeonButton(
                    text = "Descartar",
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                )
            }
        }
    }
}


@Composable
fun EnvironmentSelector(
    selectedEnvironment: TrainingEnvironment,
    onEnvironmentSelected: (TrainingEnvironment) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        TrainingEnvironment.entries.forEach { env ->
            FilterChip(
                selected = env == selectedEnvironment,
                onClick = { onEnvironmentSelected(env) },
                label = { Text(getEnvLabel(env), style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        }
    }
}

@Composable
fun GymChainSearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.gym_chain_label)) },
        placeholder = { Text(stringResource(R.string.gym_chain_placeholder)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
private fun getEnvLabel(env: TrainingEnvironment): String {
    return when (env) {
        TrainingEnvironment.BODYWEIGHT -> stringResource(R.string.env_bodyweight)
        TrainingEnvironment.DUMBBELLS -> stringResource(R.string.env_dumbbells)
        TrainingEnvironment.CHAIN_GYM -> stringResource(R.string.env_chain_gym)
    }
}
