package com.example.feature.mediadetail

import com.example.core.domain.model.media.Media

data class MediaDetailUiState(
    val media: Media? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
