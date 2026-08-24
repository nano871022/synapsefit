package co.japl.android.synapsefit.ui

import androidx.compose.ui.graphics.Color
import co.japl.android.synapsefit.ui.theme.BackgroundDark
import co.japl.android.synapsefit.ui.theme.PrimaryCyan
import co.japl.android.synapsefit.ui.theme.Spacing
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {
    @Test
    fun testColorTokens() {
        assertEquals(Color(0xFF101416), BackgroundDark)
        assertEquals(Color(0xFF00F5FF), PrimaryCyan)
    }

    @Test
    fun testSpacingDefaults() {
        val spacing = Spacing()
        assertEquals(16, spacing.marginEdge.value.toInt())
        assertEquals(16, spacing.cardPadding.value.toInt())
        assertEquals(8, spacing.small.value.toInt())
    }
}
