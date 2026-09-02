@file:Suppress("LongParameterList", "FunctionNaming", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import co.com.japl.ui.theme.MaterialThemeComposeUI

@Composable
fun AnatomicalInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unitLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        trailingIcon = {
            Text(
                text = unitLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        enabled = enabled,
        isError = isError,
        supportingText =
            if (isError && errorMessage != null) {
                { Text(text = errorMessage) }
            } else {
                null
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun AnatomicalInputFieldPreview() {
    MaterialThemeComposeUI {
        AnatomicalInputField(
            value = "75.5",
            onValueChange = {},
            label = "Peso",
            unitLabel = "kg",
        )
    }
}
