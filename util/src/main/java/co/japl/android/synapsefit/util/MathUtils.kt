package co.japl.android.synapsefit.util

import kotlin.math.pow
import kotlin.math.round

object MathUtils {
    private const val PERCENTAGE_FACTOR = 100.0

    fun roundToDecimals(
        value: Double,
        decimals: Int,
    ): Double {
        require(decimals >= 0) { "Decimals must be non-negative" }
        val factor = 10.0.pow(decimals)
        return round(value * factor) / factor
    }

    fun calculateDelta(
        current: Double,
        previous: Double,
    ): Double {
        return current - previous
    }

    fun calculatePercentageChange(
        current: Double,
        previous: Double,
    ): Double {
        if (previous == 0.0) return 0.0
        return ((current - previous) / previous) * PERCENTAGE_FACTOR
    }

    fun calculateAverage(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        return values.sum() / values.size
    }

    fun calculateTotalVolume(
        reps: Int,
        weightKg: Double,
    ): Double {
        require(reps >= 0) { "Reps cannot be negative" }
        require(weightKg >= 0.0) { "Weight cannot be negative" }
        return reps * weightKg
    }
}
