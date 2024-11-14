package com.example.feature.screens.medialist

import com.example.core.domain.model.MediaListItem

data class MediaListUiState(
    val mediaListByPage: List<List<MediaListItem>?> = List(7) { emptyList() },
    val dayOffset: Int = 0,
    val hasNextPage: Boolean? = false,
    val pageNumber: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
)
