package com.example.core.domain.model.media

enum class MediaSeason {
    WINTER,
    SPRING,
    SUMMER,
    FALL,
    UNKNOWN,
    ;

    companion object {
        val validSeasons = entries.filter { it != UNKNOWN }
    }
}
