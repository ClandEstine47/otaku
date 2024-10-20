package com.example.feature.common

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media

sealed interface MediaListItem {
    data class MediaListType(val media: Media) : MediaListItem

    data class ScheduleType(val schedule: AiringSchedule) : MediaListItem
}
