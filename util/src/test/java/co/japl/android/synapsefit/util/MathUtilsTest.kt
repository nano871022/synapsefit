package co.japl.android.synapsefit.util

import org.junit.Assert.assertEquals
import org.junit.Test

class MathUtilsTest {
    @Test
    fun testRoundToDecimals() {
        assertEquals(75.46, MathUtils.roundToDecimals(75.4567, 2), 0.0001)
        assertEquals(75.5, MathUtils.roundToDecimals(75.4567, 1), 0.0001)
        assertEquals(75.0, MathUtils.roundToDecimals(75.4567, 0), 0.0001)
    }

    @Test
    fun testCalculateDelta() {
        assertEquals(-2.5, MathUtils.calculateDelta(70.0, 72.5), 0.0001)
        assertEquals(3.2, MathUtils.calculateDelta(83.2, 80.0), 0.0001)
    }

    @Test
    fun testCalculatePercentageChange() {
        assertEquals(10.0, MathUtils.calculatePercentageChange(110.0, 100.0), 0.0001)
        assertEquals(-20.0, MathUtils.calculatePercentageChange(80.0, 100.0), 0.0001)
        assertEquals(0.0, MathUtils.calculatePercentageChange(100.0, 0.0), 0.0001)
    }

    @Test
    fun testCalculateAverage() {
        val values = listOf(10.0, 20.0, 30.0, 40.0)
        assertEquals(25.0, MathUtils.calculateAverage(values), 0.0001)
        assertEquals(0.0, MathUtils.calculateAverage(emptyList()), 0.0001)
    }

    @Test
    fun testCalculateTotalVolume() {
        assertEquals(1200.0, MathUtils.calculateTotalVolume(12, 100.0), 0.0001)
        assertEquals(0.0, MathUtils.calculateTotalVolume(0, 100.0), 0.0001)
    }
}
