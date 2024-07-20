package com.example.core.domain.model.user.statistic

interface UserStatisticsDetail {
    val count: Int
    val meanScore: Double
    val minutesWatched: Int
    val chaptersRead: Int
    val mediaIds: List<Int>
}