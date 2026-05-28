package com.example.feature.screens.medialist

import com.example.core.domain.model.MediaListItem
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaListStatus

data class MediaListUiState(
    val mediaListByPage: List<List<MediaListItem>?> = List(7) { emptyList() },
    val userListTabCounts: List<Int> = emptyList(),
    val userListMedia: List<Media> = emptyList(),
    val dayOffset: Int = 0,
    val hasNextPage: Boolean? = false,
    val pageNumber: Int = 1,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasLoadedInitialData: Boolean = false,
    val error: String? = null,
    val selectedStatus: MediaListStatus? = MediaListStatus.CURRENT,
)
