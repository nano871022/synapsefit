@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList", "MaxLineLength", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.workout.AICoachGeneratorUiState
import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.ui.components.LlmStateDialog
import co.japl.android.synapsefit.ui.components.NeonButton

@Composable
fun AICoachGeneratorScreen(
    state: AICoachGeneratorUiState,
    onLocationSelected: (TrainingLocation) -> Unit = {},
    onEquipmentSelected: (EquipmentPreference) -> Unit = {},
    onGymChainQueryChange: (String) -> Unit,
    onDaysPerWeekChange: (String) -> Unit = {},
    onPromptContextChange: (String) -> Unit,
    onOptimizeClick: () -> Unit = {},
    onGenerateClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onDismissLlmDialog: () -> Unit = {},
    onSelectLlmConfig: (String) -> Unit = {},
    onConfigureLlmRedirect: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LlmStateDialog(
        state = state.llmConfigState,
        onDismissRequest = onDismissLlmDialog,
        onSelectConfig = onSelectLlmConfig,
        onConfigureRedirect = onConfigureLlmRedirect,
    )

    if (state.isFetchingMedia) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.fetching_media_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.fetching_media_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LinearProgressIndicator(
                        progress = { state.mediaProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = "Entrenador Inteligente IA",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "Ubicación de Entrenamiento",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LocationSelector(
            selectedLocation = state.selectedLocation,
            onLocationSelected = onLocationSelected,
        )

        if (state.selectedLocation == TrainingLocation.GYM) {
            Text(
                text = stringResource(R.string.gym_chain_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GymChainSearchInput(
                query = state.gymChainQuery,
                onQueryChange = onGymChainQueryChange,
            )
        }

        Text(
            text = "Equipamiento Disponible",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        EquipmentSelector(
            selectedEquipment = state.selectedEquipment,
            onEquipmentSelected = onEquipmentSelected,
        )

        Text(
            text = "Días de entrenamiento por semana",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = state.daysPerWeek,
            onValueChange = { days ->
                val filtered = days.filter { it.isDigit() }
                if (filtered.isEmpty() || (filtered.toIntOrNull() ?: 0) in 1..7) {
                    onDaysPerWeekChange(filtered)
                }
            },
            label = { Text("Días por semana (1 - 7)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        Text(
            text = "Objetivo y Enfoque Personalizado",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = state.promptContext,
            onValueChange = onPromptContextChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            placeholder = { Text("Ej: Quiero ganar masa muscular en piernas y torso, enfocándome en ejercicios compuestos...") },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            NeonButton(
                text = if (state.isOptimizing) "Optimizando..." else "Optimizar Prompt",
                onClick = onOptimizeClick,
                enabled = !state.isGenerating && !state.isOptimizing && state.promptContext.isNotBlank(),
                modifier = Modifier.weight(1f),
            )
            NeonButton(
                text = if (state.isGenerating) "Generando..." else "Generar Rutina con IA",
                onClick = onGenerateClick,
                enabled = !state.isGenerating && !state.isOptimizing,
                modifier = Modifier.weight(1f),
            )
        }

        if (state.generationError != null) {
            Text(
                text = state.generationError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraSmall),
            )
        }

        if (state.generatedPlan != null) {
            PlanPreview(
                plan = state.generatedPlan,
                exercises = state.generatedExercises,
                onAccept = onAcceptClick,
                onDiscard = onDiscardClick,
                isLoading = state.isLoading,
            )
        } else {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .size(120.dp),
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
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = plan.goalDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            val groupedExercises = exercises.groupBy { it.day }
            groupedExercises.forEach { (dayNumber, dayExercises) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    ) {
                        Text(
                            text = "Día $dayNumber",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        dayExercises.forEach { exercise ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    val rawName = exercise.name.trim()
                                    val hasParentheses = rawName.contains("(") && rawName.contains(")")
                                    val title = if (hasParentheses) rawName.substringBefore("(").trim() else rawName
                                    val description = if (hasParentheses) rawName.substringAfter("(").substringBefore(")").trim() else ""

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "• $title",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        if (description.isNotBlank()) {
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 12.dp),
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${exercise.targetSets}x${exercise.targetReps}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                Text(
                                    text = "Músculo: ${exercise.muscleGroup} | Descanso: ${exercise.restSeconds}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 12.dp),
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NeonButton(
                    text = "Aceptar",
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                )
                NeonButton(
                    text = "Descartar",
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                )
            }
        }
    }
}

@Composable
fun LocationSelector(
    selectedLocation: TrainingLocation,
    onLocationSelected: (TrainingLocation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        TrainingLocation.entries.forEach { loc ->
            FilterChip(
                selected = loc == selectedLocation,
                onClick = { onLocationSelected(loc) },
                label = { Text(getLocationLabel(loc), style = MaterialTheme.typography.labelSmall) },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EquipmentSelector(
    selectedEquipment: EquipmentPreference,
    onEquipmentSelected: (EquipmentPreference) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        maxItemsInEachRow = 2,
    ) {
        EquipmentPreference.entries.forEach { equip ->
            FilterChip(
                selected = equip == selectedEquipment,
                onClick = { onEquipmentSelected(equip) },
                label = { Text(getEquipmentLabel(equip), style = MaterialTheme.typography.labelSmall) },
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
private fun getLocationLabel(loc: TrainingLocation): String {
    return when (loc) {
        TrainingLocation.HOME -> stringResource(R.string.loc_home)
        TrainingLocation.GYM -> stringResource(R.string.loc_gym)
    }
}

@Composable
private fun getEquipmentLabel(equip: EquipmentPreference): String {
    return when (equip) {
        EquipmentPreference.DUMBBELLS -> stringResource(R.string.equip_dumbbells)
        EquipmentPreference.CALISTHENICS -> stringResource(R.string.equip_calisthenics)
        EquipmentPreference.MACHINES -> stringResource(R.string.equip_machines)
        EquipmentPreference.NO_PREFERENCE -> stringResource(R.string.equip_none)
    }
}

@Preview(showBackground = true)
@Composable
private fun AICoachGeneratorScreenPreview() {
    MaterialThemeComposeUI {
        AICoachGeneratorScreen(
            state =
                AICoachGeneratorUiState(
                    selectedLocation = TrainingLocation.HOME,
                    selectedEquipment = EquipmentPreference.DUMBBELLS,
                    daysPerWeek = "4",
                    promptContext = "Ganar masa muscular en brazos y pecho",
                ),
            onLocationSelected = {},
            onEquipmentSelected = {},
            onGymChainQueryChange = {},
            onPromptContextChange = {},
            onGenerateClick = {},
            onAcceptClick = {},
            onDiscardClick = {},
        )
    }
}
