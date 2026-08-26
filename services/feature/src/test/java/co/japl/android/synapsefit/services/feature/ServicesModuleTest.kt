package co.japl.android.synapsefit.services.feature

import org.junit.Assert.assertEquals
import org.junit.Test

class ServicesModuleTest {
    @Test
    fun testServicesModule() {
        val placeholder = ServicesPlaceholder()
        assertEquals("services-feature", placeholder.moduleName)
    }
}
