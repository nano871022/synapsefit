package co.japl.android.synapsefit.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val DEFAULT_ZONE_ID: ZoneId = ZoneId.systemDefault()

    fun epochToLocalDateTime(
        epochMilli: Long,
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId)
    }

    fun epochToLocalDate(
        epochMilli: Long,
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): LocalDate {
        return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDate()
    }

    fun localDateToEpoch(
        localDate: LocalDate,
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): Long {
        return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun localDateTimeToEpoch(
        localDateTime: LocalDateTime,
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): Long {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    fun formatEpoch(
        epochMilli: Long,
        pattern: String = "yyyy-MM-dd HH:mm",
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return epochToLocalDateTime(epochMilli, zoneId).format(formatter)
    }

    fun formatLocalDate(
        localDate: LocalDate,
        pattern: String = "yyyy-MM-dd",
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return localDate.format(formatter)
    }

    fun formatYearMonth(
        yearMonth: YearMonth,
        pattern: String = "yyyy-MM",
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return yearMonth.format(formatter)
    }

    @Suppress("MagicNumber")
    fun calculateElapsedTimeSeconds(
        startTimestamp: Long,
        currentTimestamp: Long = System.currentTimeMillis(),
    ): Long {
        if (startTimestamp <= 0L || currentTimestamp < startTimestamp) return 0L
        return (currentTimestamp - startTimestamp) / 1000L
    }

    fun calculateAgeYears(
        birthDateString: String?,
        currentDate: LocalDate = LocalDate.now(),
    ): Int? {
        if (birthDateString.isNullOrBlank()) return null
        return try {
            val birthDate = LocalDate.parse(birthDateString.trim())
            java.time.Period.between(birthDate, currentDate).years
        } catch (_: Exception) {
            null
        }
    }
}
