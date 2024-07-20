package com.example.core.domain.model.user.statistic

data class UserReleaseYearStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val releaseYear: Int = 0
): UserStatisticsDetail