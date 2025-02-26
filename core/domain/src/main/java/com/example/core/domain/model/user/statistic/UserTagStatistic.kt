package com.example.core.domain.model.user.statistic

import com.example.core.domain.model.media.MediaTag

data class UserTagStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val tag: MediaTag? = null,
) : UserStatisticsDetail
