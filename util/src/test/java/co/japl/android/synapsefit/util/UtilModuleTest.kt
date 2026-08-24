package co.japl.android.synapsefit.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilModuleTest {
    @Test
    fun testUtilModule() {
        val placeholder = UtilPlaceholder()
        assertEquals("util", placeholder.moduleName)
    }
}
