@file:Suppress(
    "FunctionNaming",
    "LongMethod",
    "CyclomaticComplexMethod",
    "LongParameterList",
    "UnusedPrivateMember",
    "MagicNumber",
)

package co.japl.android.synapsefit.app.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.profile.UserProfileUiState
import co.japl.android.synapsefit.ui.components.AnatomicalInputField
import co.japl.android.synapsefit.ui.components.NeonButton
import co.japl.android.synapsefit.util.DateTimeUtils
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
    onRecalculateMedicalEvaluation: () -> Unit = {},
    onRetryMedicalConditions: () -> Unit = {},
    onDismissMedicalDialog: () -> Unit = {},
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
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showAllRecommendationsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoading, state.isSavedSuccess) {
        if (!state.isLoading && state.errorMessage == null) {
            isFullNameEditable = state.fullName.isBlank()
            isBirthDateEditable = state.birthDate.isBlank()
            isGenderEditable = state.gender.isBlank()
            isHeightCmEditable = state.heightCm.isBlank()
            isBloodTypeEditable = state.bloodType.isBlank()
            isMedicalConditionsEditable = state.medicalConditions.isBlank()
        }
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
                onValueChange = { },
                readOnly = true,
                enabled = isBirthDateEditable,
                label = { Text(stringResource(R.string.birth_date)) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (isBirthDateEditable) {
                                showDatePickerDialog = true
                            }
                        },
                        enabled = isBirthDateEditable,
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Birth Date")
                    }
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable(enabled = isBirthDateEditable) {
                            showDatePickerDialog = true
                        },
                singleLine = true,
            )
            IconButton(onClick = { isBirthDateEditable = !isBirthDateEditable }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Birth Date")
            }
        }

        if (showDatePickerDialog && isBirthDateEditable) {
            val initialMillis =
                remember(state.birthDate) {
                    val epoch = DateTimeUtils.parseIsoDateToEpoch(state.birthDate)
                    if (epoch > 0L) epoch else System.currentTimeMillis()
                }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                                cal.timeInMillis = selectedMillis
                                val formatted =
                                    String.format(
                                        Locale.US,
                                        "%04d-%02d-%02d",
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH) + 1,
                                        cal.get(Calendar.DAY_OF_MONTH),
                                    )
                                onBirthDateChange(formatted)
                            }
                            showDatePickerDialog = false
                        },
                    ) {
                        Text(stringResource(R.string.save_profile))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
            ) {
                DatePicker(state = datePickerState)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            NeonButton(
                text = stringResource(R.string.save_profile),
                onClick = onSaveClick,
                isLoading = state.isLoading && !state.showMedicalDialog,
                modifier = Modifier.weight(1f),
            )

            if (state.needsMedicalEvaluation) {
                NeonButton(
                    text = stringResource(R.string.recalculate_medical_recommendations),
                    onClick = onRecalculateMedicalEvaluation,
                    isLoading = state.isLoading && !state.showMedicalDialog,
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (state.latestRecommendation != null) {
            RecommendationSection(
                latestRecommendation = state.latestRecommendation,
                onViewAllClick = { showAllRecommendationsDialog = true },
            )
        }
    }

    if (showAllRecommendationsDialog) {
        AllRecommendationsDialog(
            recommendations = state.allRecommendations,
            onDismiss = { showAllRecommendationsDialog = false },
        )
    }

    if (state.showMedicalDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = onDismissMedicalDialog) {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.small),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.processing_medical_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = stringResource(R.string.processing_medical_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    if (state.isEvaluatingMedical) {
                        androidx.compose.material3.LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (state.medicalEvaluationFailed) {
                        val errorData = parseHttpError(state.medicalEvaluationError)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = errorData.code,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(errorData.titleRes),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorData.originalMessage,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                            )
                        }

                        NeonButton(
                            text = stringResource(R.string.retry_medical_recommendations),
                            onClick = onRetryMedicalConditions,
                            isLoading = state.isEvaluatingMedical,
                        )
                    }

                    TextButton(onClick = onDismissMedicalDialog) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationSection(
    latestRecommendation: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text(
            text = stringResource(R.string.latest_medical_recommendation),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = latestRecommendation,
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        TextButton(
            onClick = onViewAllClick,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.view_all_recommendations))
        }
    }
}

@Composable
fun AllRecommendationsDialog(
    recommendations: List<co.japl.android.synapsefit.core.domain.model.MedicalRecommendation>,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxSize(0.8f),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text = stringResource(R.string.all_recommendations_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    items(recommendations) { rec ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                        ) {
                            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                                Text(
                                    text = DateTimeUtils.formatEpoch(rec.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.padding(top = 4.dp))
                                Text(
                                    text = rec.result,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    MaterialThemeComposeUI {
        UserProfileScreen(
            state =
                UserProfileUiState(
                    fullName = "Atleta SynapseFit",
                    birthDate = "1995-05-20",
                    gender = "HOMBRE",
                    heightCm = "175",
                    bloodType = "O+",
                    medicalConditions = "Ninguna",
                ),
            onFullNameChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onHeightCmChange = {},
            onBloodTypeChange = {},
            onMedicalConditionsChange = {},
            onSaveClick = {},
        )
    }
}
