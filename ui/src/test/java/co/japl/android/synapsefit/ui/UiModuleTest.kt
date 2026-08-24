package co.japl.android.synapsefit.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UiModuleTest {
    @Test
    fun testUiModule() {
        val placeholder = UiPlaceholder()
        assertEquals("ui", placeholder.moduleName)
    }
}
