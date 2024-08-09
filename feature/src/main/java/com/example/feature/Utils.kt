package com.example.feature

import com.example.core.domain.model.AnimeSeason
import com.example.core.domain.model.media.MediaSeason
import java.time.LocalDateTime
import java.time.Month

object Utils {
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
}
