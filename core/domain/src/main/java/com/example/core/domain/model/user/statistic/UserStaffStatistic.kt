package com.example.core.domain.model.user.statistic

import com.example.core.domain.model.staff.Staff

data class UserStaffStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val staff: Staff? = null,
) : UserStatisticsDetail
