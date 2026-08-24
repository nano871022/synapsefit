package co.japl.android.synapsefit.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CoreModuleTest {
    @Test
    fun testCoreModuleHasNoAndroidDependencies() {
        val placeholder = CorePlaceholder()
        assertEquals("core", placeholder.moduleName)

        val importsAndroid =
            try {
                Class.forName("android.os.Bundle")
                true
            } catch (e: ClassNotFoundException) {
                assertFalse(e.message, false)
                false
            }
        assertFalse("Core module must not depend on Android framework!", importsAndroid)
    }
}
