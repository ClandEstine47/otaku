package com.example.feature.manga

import com.example.core.domain.model.media.Media

data class MangaUiState(
    val trendingNowMedia: List<Media>? = null,
//    val currentSeasonMedia: List<Media>? = null,
//    val recentlyUpdatedMedia: List<AiringSchedule>? = null,
//    val popularMedia: List<Media>? = null,
//    val nextSeasonMedia: List<Media>? = null,
//    val nowAnimeSeason: AnimeSeason,
//    val nextAnimeSeason: AnimeSeason,
    val isLoading: Boolean = false,
    val error: String? = null,
)
