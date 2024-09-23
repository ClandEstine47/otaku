package com.example.feature

import com.example.core.domain.model.AnimeSeason
import com.example.core.domain.model.media.MediaSeason
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Utils {
    private val defaultZoneOffset get() = ZonedDateTime.now(ZoneId.systemDefault()).offset

    private fun LocalDateTime.season(): MediaSeason {
        return when (this.month) {
            Month.JANUARY, Month.FEBRUARY, Month.DECEMBER -> MediaSeason.WINTER
            Month.MARCH, Month.APRIL, Month.MAY -> MediaSeason.SPRING
            Month.JUNE, Month.JULY, Month.AUGUST -> MediaSeason.SUMMER
            Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> MediaSeason.FALL
            else -> MediaSeason.UNKNOWN
        }
    }

    fun LocalDateTime.currentAnimeSeason(): AnimeSeason {
        var animeSeason = AnimeSeason(year = year, season = season())
        if (month == Month.DECEMBER) {
            animeSeason = animeSeason.copy(year = year + 1)
        }
        return animeSeason
    }

    fun LocalDateTime.nextAnimeSeason(): AnimeSeason {
        val current = currentAnimeSeason()
        return when (current.season) {
            MediaSeason.WINTER -> current.copy(season = MediaSeason.SPRING)
            MediaSeason.SPRING -> current.copy(season = MediaSeason.SUMMER)
            MediaSeason.SUMMER -> current.copy(season = MediaSeason.FALL)
            MediaSeason.FALL ->
                current.copy(
                    season = MediaSeason.WINTER,
                    year = year + 1,
                )

            else -> current
        }
    }

    /**
     * @param dayOffset Integer representing the day offset from today (0 for today, 1 for tomorrow, etc.)
     * @param isEndOfDay Boolean to determine if the end of the day timestamp is needed
     * @returns the requested day's timestamp (start or end of the day)
     */
    fun LocalDateTime.getDayTimestamp(
        dayOffset: Int,
        isEndOfDay: Boolean,
    ): Long {
        val targetDate = this.plusDays(dayOffset.toLong()).toLocalDate()
        return if (isEndOfDay) {
            targetDate.plusDays(1).atStartOfDay().minusNanos(1).toInstant(defaultZoneOffset).epochSecond
        } else {
            targetDate.atStartOfDay().toInstant(defaultZoneOffset).epochSecond
        }
    }

    fun displayInDayDateTimeFormat(seconds: Int): String {
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = Date(seconds * 1000L)
        return dateFormat.format(date)
    }

    fun formatDateToText(
        year: Int,
        month: Int,
        day: Int,
    ): String {
        val dateTime = LocalDateTime.of(year, month, day, 0, 0)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        return dateTime.format(formatter)
    }
}
