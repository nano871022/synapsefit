@file:Suppress("FunctionNaming", "LongMethod", "CyclomaticComplexMethod", "LongParameterList")

package co.japl.android.synapsefit.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.ui.components.AnatomicalInputField
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: UserProfileUiState,
    onFullNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onHeightCmChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onMedicalConditionsChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.marginEdge)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.user_profile),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (state.isSavedSuccess) {
            Text(
                text = stringResource(R.string.profile_saved),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        OutlinedTextField(
            value = state.fullName,
            onValueChange = onFullNameChange,
            label = { Text(stringResource(R.string.user_full_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = state.birthDate,
            onValueChange = onBirthDateChange,
            label = { Text(stringResource(R.string.birth_date) + " (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Text(
            text = stringResource(R.string.gender),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val genderOptions =
                listOf(
                    "HOMBRE" to stringResource(R.string.gender_male),
                    "MUJER" to stringResource(R.string.gender_female),
                    "OTRO" to stringResource(R.string.gender_other),
                )

            genderOptions.forEach { (key, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    RadioButton(
                        selected = state.gender == key,
                        onClick = { onGenderChange(key) },
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        AnatomicalInputField(
            value = state.heightCm,
            onValueChange = onHeightCmChange,
            label = stringResource(R.string.height_cm),
            unitLabel = stringResource(R.string.cm_unit),
        )

        ExposedDropdownMenuBox(
            expanded = bloodTypeExpanded,
            onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded },
        ) {
            OutlinedTextField(
                value = state.bloodType,
                onValueChange = onBloodTypeChange,
                label = { Text(stringResource(R.string.blood_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = bloodTypeExpanded,
                onDismissRequest = { bloodTypeExpanded = false },
            ) {
                bloodTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onBloodTypeChange(type)
                            bloodTypeExpanded = false
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.medicalConditions,
            onValueChange = onMedicalConditionsChange,
            label = { Text(stringResource(R.string.medical_conditions)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
        )

        NeonButton(
            text = stringResource(R.string.save_profile),
            onClick = onSaveClick,
            isLoading = state.isLoading,
        )
    }
}
