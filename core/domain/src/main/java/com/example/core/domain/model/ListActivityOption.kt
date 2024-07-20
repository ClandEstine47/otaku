package com.example.core.domain.model

import com.example.core.domain.model.media.MediaListStatus

data class ListActivityOption(
    val disabled: Boolean = false,
    val type: MediaListStatus? = null
)