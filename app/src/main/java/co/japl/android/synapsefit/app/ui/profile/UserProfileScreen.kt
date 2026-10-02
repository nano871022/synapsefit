@file:Suppress("FunctionNaming", "LongMethod", "CyclomaticComplexMethod", "MaxLineLength", "UnusedPrivateMember")

package co.japl.android.synapsefit.app.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.spacing
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.app.controller.profile.UserProfileUiState
import co.japl.android.synapsefit.ui.components.LlmStateDialog
import co.japl.android.synapsefit.ui.components.NeonButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    onDismissLlmDialog: () -> Unit = {},
    onSelectLlmConfig: (String) -> Unit = {},
    onConfigureLlmRedirect: () -> Unit = {},
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

    LlmStateDialog(
        state = state.llmConfigState,
        onDismissRequest = onDismissLlmDialog,
        onSelectConfig = onSelectLlmConfig,
        onConfigureRedirect = onConfigureLlmRedirect,
    )

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

    if (state.showMedicalDialog) {
        MedicalEvaluationProgressDialog(
            isEvaluating = state.isEvaluatingMedical,
            evaluationFailed = state.medicalEvaluationFailed,
            errorMessage = state.medicalEvaluationError,
            onDismiss = onDismissMedicalDialog,
            onRetry = onRetryMedicalConditions,
        )
    }

    if (showDatePickerDialog) {
        ProfileDatePickerDialog(
            onDateSelected = { selectedDate ->
                onBirthDateChange(selectedDate)
                showDatePickerDialog = false
            },
            onDismiss = { showDatePickerDialog = false },
        )
    }

    if (showAllRecommendationsDialog) {
        AllRecommendationsDialog(
            recommendations = state.allRecommendations,
            onDismiss = { showAllRecommendationsDialog = false },
        )
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
            text = "Perfil de Usuario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        OutlinedTextField(
            value = state.fullName,
            onValueChange = onFullNameChange,
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = isFullNameEditable,
            trailingIcon = {
                IconButton(onClick = { isFullNameEditable = !isFullNameEditable }) {
                    Icon(
                        imageVector = if (isFullNameEditable) Icons.Default.Lock else Icons.Default.Edit,
                        contentDescription = "Editar nombre",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.birthDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Nacimiento") },
                placeholder = { Text("AAAA-MM-DD") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = isBirthDateEditable,
                trailingIcon = {
                    Row {
                        if (isBirthDateEditable) {
                            IconButton(onClick = { showDatePickerDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Seleccionar fecha",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                        IconButton(onClick = { isBirthDateEditable = !isBirthDateEditable }) {
                            Icon(
                                imageVector = if (isBirthDateEditable) Icons.Default.Lock else Icons.Default.Edit,
                                contentDescription = "Editar fecha nacimiento",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
            )
        }

        OutlinedTextField(
            value = state.gender,
            onValueChange = onGenderChange,
            label = { Text("Género") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = isGenderEditable,
            trailingIcon = {
                IconButton(onClick = { isGenderEditable = !isGenderEditable }) {
                    Icon(
                        imageVector = if (isGenderEditable) Icons.Default.Lock else Icons.Default.Edit,
                        contentDescription = "Editar género",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        OutlinedTextField(
            value = state.heightCm,
            onValueChange = onHeightCmChange,
            label = { Text("Estatura (cm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = isHeightCmEditable,
            trailingIcon = {
                IconButton(onClick = { isHeightCmEditable = !isHeightCmEditable }) {
                    Icon(
                        imageVector = if (isHeightCmEditable) Icons.Default.Lock else Icons.Default.Edit,
                        contentDescription = "Editar estatura",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.bloodType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Sangre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isBloodTypeEditable,
                trailingIcon = {
                    Row {
                        IconButton(onClick = { isBloodTypeEditable = !isBloodTypeEditable }) {
                            Icon(
                                imageVector = if (isBloodTypeEditable) Icons.Default.Lock else Icons.Default.Edit,
                                contentDescription = "Editar tipo de sangre",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
            )
            if (isBloodTypeEditable) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clickable { bloodTypeExpanded = true },
                )
            }
            DropdownMenu(
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
            label = { Text("Condiciones / Enfermedades Médicas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = isMedicalConditionsEditable,
            trailingIcon = {
                IconButton(onClick = { isMedicalConditionsEditable = !isMedicalConditionsEditable }) {
                    Icon(
                        imageVector = if (isMedicalConditionsEditable) Icons.Default.Lock else Icons.Default.Edit,
                        contentDescription = "Editar condiciones médicas",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        if (state.needsMedicalEvaluation) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Evaluación Médica Pendiente",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Text(
                            text = "Sus condiciones médicas no han sido evaluadas por la IA deportiva.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                    NeonButton(
                        text = "Reevaluar",
                        onClick = onRecalculateMedicalEvaluation,
                    )
                }
            }
        }

        if (!state.latestRecommendation.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "Recomendaciones Deportivas IA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        if (state.allRecommendations.size > 1) {
                            IconButton(onClick = { showAllRecommendationsDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Ver historial",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    Text(
                        text = state.latestRecommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (state.isSavedSuccess) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Perfil guardado con éxito",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        NeonButton(
            text = if (state.isLoading) "Guardando..." else "Guardar Perfil",
            onClick = onSaveClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun MedicalEvaluationProgressDialog(
    isEvaluating: Boolean,
    evaluationFailed: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    val parsedError = if (evaluationFailed) parseHttpError(errorMessage) else null

    Dialog(
        onDismissRequest = { if (!isEvaluating) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = !isEvaluating, dismissOnClickOutside = !isEvaluating),
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
                if (isEvaluating) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Evaluando perfil médico...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Procesando condiciones de salud con la IA deportiva",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else if (evaluationFailed) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        text = parsedError?.let { "${it.code} - ${stringResource(it.titleRes)}" } ?: "Error en la Evaluación Médica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = parsedError?.originalMessage ?: errorMessage ?: "Ocurrió un error al procesar las restricciones médicas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Continuar sin evaluar")
                        }
                        NeonButton(
                            text = "Reintentar",
                            onClick = onRetry,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        val formattedDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        onDateSelected(formattedDate)
                    }
                },
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun AllRecommendationsDialog(
    recommendations: List<co.japl.android.synapsefit.core.domain.model.MedicalRecommendation>,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(MaterialTheme.spacing.medium)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    text = "Historial de Recomendaciones Médicas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                recommendations.forEachIndexed { index, rec ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.small)) {
                            Text(
                                text = "Evaluación #${recommendations.size - index}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rec.result,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
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
