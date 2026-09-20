package co.japl.android.synapsefit.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

class DateTimeUtilsTest {
    private val testZoneId = ZoneId.of("UTC")
    private val testLocale = Locale.US

    @Test
    fun testEpochAndLocalDateTimeConversion() {
        val epochMilli = 1700000000000L
        val localDateTime = DateTimeUtils.epochToLocalDateTime(epochMilli, testZoneId)

        assertEquals(2023, localDateTime.year)
        assertEquals(11, localDateTime.monthValue)
        assertEquals(14, localDateTime.dayOfMonth)
        assertEquals(22, localDateTime.hour)

        val backToEpoch = DateTimeUtils.localDateTimeToEpoch(localDateTime, testZoneId)
        assertEquals(epochMilli, backToEpoch)
    }

    @Test
    fun testEpochAndLocalDateConversion() {
        val date = LocalDate.of(2024, 5, 20)
        val epochMilli = DateTimeUtils.localDateToEpoch(date, testZoneId)
        val dateFromEpoch = DateTimeUtils.epochToLocalDate(epochMilli, testZoneId)

        assertEquals(date, dateFromEpoch)
    }

    @Test
    fun testFormatEpoch() {
        val epochMilli = 1700000000000L
        val formatted = DateTimeUtils.formatEpoch(epochMilli, "yyyy-MM-dd HH:mm", testZoneId, testLocale)
        assertEquals("2023-11-14 22:13", formatted)
    }

    @Test
    fun testFormatLocalDate() {
        val date = LocalDate.of(2025, 1, 15)
        val formatted = DateTimeUtils.formatLocalDate(date, "dd/MM/yyyy", testLocale)
        assertEquals("15/01/2025", formatted)
    }

    @Test
    fun testFormatYearMonth() {
        val yearMonth = YearMonth.of(2025, 3)
        val formatted = DateTimeUtils.formatYearMonth(yearMonth, "MMMM yyyy", testLocale)
        assertEquals("March 2025", formatted)
    }

    @Test
    fun testCalculateElapsedTimeSeconds() {
        val now = 1000000L
        val start = 900000L
        val elapsed = DateTimeUtils.calculateElapsedTimeSeconds(start, now)
        assertEquals(100L, elapsed)
    }

    @Test
    fun testFormatElapsedTime() {
        assertEquals("00:00", DateTimeUtils.formatElapsedTime(0L))
        assertEquals("00:45", DateTimeUtils.formatElapsedTime(45L))
        assertEquals("05:23", DateTimeUtils.formatElapsedTime(323L))
        assertEquals("01:01:05", DateTimeUtils.formatElapsedTime(3665L))
    }
}
