package com.example.core.domain.model.media

enum class MediaSort {
    SCORE,
    POPULARITY,
    TRENDING,
    FAVOURITES,
    ;

    companion object {
        val types = MediaSort.entries.toList()
    }
}
