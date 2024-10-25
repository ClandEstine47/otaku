package com.example.feature.search

import com.example.core.domain.model.media.Media

data class MediaSearchUiState(
    val mediaList: List<Media>? = null,
    val pageNumber: Int = 1,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
