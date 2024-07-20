package com.example.core.domain.model.media

data class MediaTrend(
    val mediaId: Int? = null,
    val date: Int? = null,
    val trending: Int? = null,
    val averageScore: Int? = null,
    val popularity: Int? = null,
    val inProgress: Int? = null,
    val releasing: Boolean,
    val episode: Int? = null,
    val media: Media? = null
)