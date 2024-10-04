package com.example.feature.mediadetail

import com.example.core.domain.model.media.Media
import com.example.core.domain.model.thread.Thread

data class MediaDetailUiState(
    val media: Media? = null,
    val mediaThreads: List<Thread>? = null,
    val isLoadingMediaDetails: Boolean = false,
    val isLoadingMediaThreads: Boolean = false,
    val error: String? = null,
)
