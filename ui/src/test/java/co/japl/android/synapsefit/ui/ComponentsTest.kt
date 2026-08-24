package co.japl.android.synapsefit.ui

import co.japl.android.synapsefit.ui.components.GraphDataPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class ComponentsTest {
    @Test
    fun testGraphDataPointCreation() {
        val point = GraphDataPoint(xLabel = "Jan", value = 75.5f)
        assertEquals("Jan", point.xLabel)
        assertEquals(75.5f, point.value, 0.001f)
    }
}
