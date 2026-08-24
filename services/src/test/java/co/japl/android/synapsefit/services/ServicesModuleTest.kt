package co.japl.android.synapsefit.services

import org.junit.Assert.assertEquals
import org.junit.Test

class ServicesModuleTest {
    @Test
    fun testServicesModule() {
        val placeholder = ServicesPlaceholder()
        assertEquals("services", placeholder.moduleName)
    }
}
