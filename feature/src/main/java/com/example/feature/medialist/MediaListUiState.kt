package com.example.feature.medialist

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media

data class MediaListUiState(
    val mediaListByPage: List<List<MediaListItem>?> = List(7) { emptyList() },
    val dayOffset: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface MediaListItem {
    data class MediaListType(val media: Media) : MediaListItem

    data class ScheduleType(val schedule: AiringSchedule) : MediaListItem
}
