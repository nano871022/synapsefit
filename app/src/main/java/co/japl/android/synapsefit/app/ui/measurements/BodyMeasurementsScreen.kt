@file:Suppress("FunctionNaming", "LongMethod", "LongParameterList")

package co.japl.android.synapsefit.app.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.AnatomicalInputField
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun BodyMeasurementsScreen(
    state: BodyMeasurementsUiState,
    onWeightChange: (String) -> Unit,
    onChestChange: (String) -> Unit,
    onWaistChange: (String) -> Unit,
    onHipChange: (String) -> Unit,
    onBicepLeftChange: (String) -> Unit,
    onBicepRightChange: (String) -> Unit,
    onThighLeftChange: (String) -> Unit,
    onThighRightChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
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
            text = stringResource(R.string.anthropometric_record),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        AnatomicalInputField(
            value = state.weightKg,
            onValueChange = onWeightChange,
            label = stringResource(R.string.weight_corp),
            unitLabel = stringResource(R.string.weight_unit),
            isError = state.errorMessage != null && state.weightKg.isBlank(),
        )

        Text(
            text = stringResource(R.string.anatomical_measurements_optional),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnatomicalInputField(
                value = state.chestCm,
                onValueChange = onChestChange,
                label = stringResource(R.string.chest),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
            AnatomicalInputField(
                value = state.waistCm,
                onValueChange = onWaistChange,
                label = stringResource(R.string.waist),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnatomicalInputField(
                value = state.hipCm,
                onValueChange = onHipChange,
                label = stringResource(R.string.hip),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
            AnatomicalInputField(
                value = state.bicepLeftCm,
                onValueChange = onBicepLeftChange,
                label = stringResource(R.string.bicep_left),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnatomicalInputField(
                value = state.bicepRightCm,
                onValueChange = onBicepRightChange,
                label = stringResource(R.string.bicep_right),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
            AnatomicalInputField(
                value = state.thighLeftCm,
                onValueChange = onThighLeftChange,
                label = stringResource(R.string.thigh_left),
                unitLabel = stringResource(R.string.cm_unit),
                modifier = Modifier.weight(1f),
            )
        }

        AnatomicalInputField(
            value = state.thighRightCm,
            onValueChange = onThighRightChange,
            label = stringResource(R.string.thigh_right),
            unitLabel = stringResource(R.string.cm_unit),
        )

        OutlinedTextField(
            value = state.notes,
            onValueChange = onNotesChange,
            label = { Text(stringResource(R.string.notes_observations)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        NeonButton(
            text = stringResource(R.string.save_measurements),
            onClick = onSaveClick,
            isLoading = state.isSaving,
            enabled = state.weightKg.isNotBlank(),
        )
    }
}
