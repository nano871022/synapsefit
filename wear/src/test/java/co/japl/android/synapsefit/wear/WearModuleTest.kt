package co.japl.android.synapsefit.wear

import org.junit.Assert.assertEquals
import org.junit.Test

class WearModuleTest {
    @Test
    fun testWearModule() {
        val placeholder = WearPlaceholder()
        assertEquals("wear", placeholder.moduleName)
    }
}
