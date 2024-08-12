package com.example.feature.anime

import com.example.core.domain.model.AnimeSeason
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media

data class AnimeUiState(
    val currentSeasonMedia: List<Media>? = null,
    val recentlyUpdatedMedia: List<AiringSchedule>? = null,
    val trendingNowMedia: List<Media>? = null,
    val popularMedia: List<Media>? = null,
    val nextSeasonMedia: List<Media>? = null,
    val nowAnimeSeason: AnimeSeason,
    val nextAnimeSeason: AnimeSeason,
    val isLoading: Boolean = false,
    val error: String? = null,
)
