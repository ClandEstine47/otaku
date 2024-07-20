package com.example.core.domain.model.medialistcollection

import com.example.core.domain.model.media.MediaList
import com.example.core.domain.model.media.MediaListStatus

data class MediaListGroup(
    val entries: List<MediaList> = listOf(),
    val name: String = "",
    val isCustomList: Boolean = false,
    val isSplitCompletedList: Boolean = false,
    val status: MediaListStatus? = null
)