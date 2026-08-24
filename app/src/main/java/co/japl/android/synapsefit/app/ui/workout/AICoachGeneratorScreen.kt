@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun AICoachGeneratorScreen(
    state: AICoachGeneratorUiState,
    onEnvironmentSelected: (TrainingEnvironment) -> Unit,
    onGymChainQueryChange: (String) -> Unit,
    onPromptContextChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
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
            minLines = 4,
        )

        NeonButton(
            text = stringResource(R.string.generate_plan_ai),
            onClick = onGenerateClick,
            isLoading = state.isGenerating,
            enabled = !state.isGenerating,
        )
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrainingEnvironment.entries.forEach { env ->
            FilterChip(
                selected = env == selectedEnvironment,
                onClick = { onEnvironmentSelected(env) },
                label = { Text(getEnvLabel(env)) },
                modifier = Modifier.weight(1f),
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
