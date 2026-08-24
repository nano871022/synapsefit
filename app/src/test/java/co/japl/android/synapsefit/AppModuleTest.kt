package co.japl.android.synapsefit

import org.junit.Assert.assertEquals
import org.junit.Test

class AppModuleTest {
    @Test
    fun testAppModule() {
        val placeholder = AppPlaceholder()
        assertEquals("app", placeholder.moduleName)
    }
}
