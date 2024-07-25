package com.example.core.domain.model.airing

import com.example.core.domain.model.media.Media

data class AiringSchedule(
    val id: Int = 0,
    val airingAt: Int = 0,
    val timeUntilAiring: Int = 0,
    val episode: Int = 0,
    val media: Media = Media(),
)
