package com.example.core.domain.model.media

import com.example.core.domain.model.ScoreFormat

data class MediaListOptions(
    var scoreFormat: ScoreFormat? = null,
    var rowOrder: String = "",
    val animeList: MediaListTypeOptions = MediaListTypeOptions(),
    val mangaList: MediaListTypeOptions = MediaListTypeOptions(),
)
