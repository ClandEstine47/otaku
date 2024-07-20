package com.example.core.domain.model

import com.example.core.domain.model.media.MediaListStatus

data class StatusDistribution(
    val status: MediaListStatus? = null,
    val amount: Int = 0
)