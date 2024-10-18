package com.example.core.domain.model.media

enum class MediaFormat {
    TV,
    TV_SHORT,
    MOVIE,
    OVA,
    ONA,
    SPECIAL,
    MUSIC,
    MANGA,
    NOVEL,
    ONE_SHOT,
    ;

    companion object {
        private val mediaFormats = MediaFormat.entries
        val animeFormats = mediaFormats.filter { it != MANGA && it != NOVEL && it != ONE_SHOT }
        val mangaFormats = mediaFormats.filterNot { it in animeFormats }
    }
}
