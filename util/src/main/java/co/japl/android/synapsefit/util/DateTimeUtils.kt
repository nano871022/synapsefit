package co.japl.android.synapsefit.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("TooManyFunctions")
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
        locale: Locale = Locale.getDefault(),
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return epochToLocalDateTime(epochMilli, zoneId).format(formatter)
    }

    fun formatLocalDate(
        localDate: LocalDate,
        pattern: String = "yyyy-MM-dd",
        locale: Locale = Locale.getDefault(),
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return localDate.format(formatter)
    }

    fun formatYearMonth(
        yearMonth: YearMonth,
        pattern: String = "yyyy-MM",
        locale: Locale = Locale.getDefault(),
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return yearMonth.format(formatter)
    }

    fun parseIsoDateToEpoch(
        dateString: String?,
        zoneId: ZoneId = DEFAULT_ZONE_ID,
    ): Long {
        if (dateString.isNullOrBlank()) return 0L
        return try {
            val localDate = LocalDate.parse(dateString.trim())
            localDateToEpoch(localDate, zoneId)
        } catch (_: Exception) {
            0L
        }
    }

    @Suppress("MagicNumber")
    fun calculateElapsedTimeSeconds(
        startTimestamps: Long?,
        currentTimestamp: Long = System.currentTimeMillis(),
    ): Long {
        val startTimestamp = startTimestamps ?: System.currentTimeMillis()
        if (startTimestamp !in 1..currentTimestamp) return 0L
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

    @Suppress("MagicNumber")
    fun formatElapsedTime(elapsedSeconds: Long): String {
        val totalSeconds = elapsedSeconds.coerceAtLeast(0L)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
}
