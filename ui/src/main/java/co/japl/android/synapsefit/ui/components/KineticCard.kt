@file:Suppress("FunctionNaming", "UnusedPrivateMember", "MagicNumber")

package co.japl.android.synapsefit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.japl.android.synapsefit.ui.theme.SynapseFitTheme
import co.japl.android.synapsefit.ui.theme.spacing

@Composable
fun KineticCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = MaterialTheme.colorScheme.outlineVariant,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
            ),
        border = borderColor?.let { BorderStroke(1.dp, it) },
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.cardPadding),
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KineticCardPreview() {
    SynapseFitTheme {
        KineticCard {
            Text(text = "Tarjeta kinetic de prueba", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
