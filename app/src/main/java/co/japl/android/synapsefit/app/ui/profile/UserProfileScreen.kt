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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    var isFullNameEditable by remember { mutableStateOf(state.fullName.isBlank()) }
    var isBirthDateEditable by remember { mutableStateOf(state.birthDate.isBlank()) }
    var isGenderEditable by remember { mutableStateOf(state.gender.isBlank()) }
    var isHeightCmEditable by remember { mutableStateOf(state.heightCm.isBlank()) }
    var isBloodTypeEditable by remember { mutableStateOf(state.bloodType.isBlank()) }
    var isMedicalConditionsEditable by remember { mutableStateOf(state.medicalConditions.isBlank()) }

    LaunchedEffect(
        state.fullName,
        state.birthDate,
        state.gender,
        state.heightCm,
        state.bloodType,
        state.medicalConditions,
    ) {
        if (state.fullName.isNotBlank()) isFullNameEditable = false
        if (state.birthDate.isNotBlank()) isBirthDateEditable = false
        if (state.gender.isNotBlank()) isGenderEditable = false
        if (state.heightCm.isNotBlank()) isHeightCmEditable = false
        if (state.bloodType.isNotBlank()) isBloodTypeEditable = false
        if (state.medicalConditions.isNotBlank()) isMedicalConditionsEditable = false
    }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.fullName,
                onValueChange = onFullNameChange,
                enabled = isFullNameEditable,
                label = { Text(stringResource(R.string.user_full_name)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            IconButton(onClick = { isFullNameEditable = !isFullNameEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Full Name")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.birthDate,
                onValueChange = onBirthDateChange,
                enabled = isBirthDateEditable,
                label = { Text(stringResource(R.string.birth_date) + " (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            IconButton(onClick = { isBirthDateEditable = !isBirthDateEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Birth Date")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            IconButton(onClick = { isGenderEditable = !isGenderEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Gender")
            }
        }

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
                        enabled = isGenderEditable,
                        onClick = { if (isGenderEditable) onGenderChange(key) },
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnatomicalInputField(
                value = state.heightCm,
                onValueChange = onHeightCmChange,
                label = stringResource(R.string.height_cm),
                unitLabel = stringResource(R.string.cm_unit),
                enabled = isHeightCmEditable,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { isHeightCmEditable = !isHeightCmEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Height")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExposedDropdownMenuBox(
                expanded = bloodTypeExpanded && isBloodTypeEditable,
                onExpandedChange = { if (isBloodTypeEditable) bloodTypeExpanded = !bloodTypeExpanded },
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = state.bloodType,
                    onValueChange = onBloodTypeChange,
                    enabled = isBloodTypeEditable,
                    label = { Text(stringResource(R.string.blood_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = bloodTypeExpanded && isBloodTypeEditable,
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
            IconButton(onClick = { isBloodTypeEditable = !isBloodTypeEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Blood Type")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.medicalConditions,
                onValueChange = onMedicalConditionsChange,
                enabled = isMedicalConditionsEditable,
                label = { Text(stringResource(R.string.medical_conditions)) },
                modifier = Modifier.weight(1f),
                minLines = 3,
                maxLines = 5,
            )
            IconButton(onClick = { isMedicalConditionsEditable = !isMedicalConditionsEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Medical Conditions")
            }
        }

        NeonButton(
            text = stringResource(R.string.save_profile),
            onClick = onSaveClick,
            isLoading = state.isLoading,
        )
    }
}
