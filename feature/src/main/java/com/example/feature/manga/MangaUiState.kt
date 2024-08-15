package com.example.feature.manga

import com.example.core.domain.model.media.Media

data class MangaUiState(
    val trendingMangaList: List<Media>? = null,
    val popularMangaList: List<Media>? = null,
    val popularManhwaList: List<Media>? = null,
    val popularNovelList: List<Media>? = null,
    val popularOneShotList: List<Media>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
