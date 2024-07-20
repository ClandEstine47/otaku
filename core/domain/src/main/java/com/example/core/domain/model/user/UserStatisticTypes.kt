package com.example.core.domain.model.user

data class UserStatisticTypes(
    val anime: UserStatistics = UserStatistics(),
    val manga: UserStatistics = UserStatistics()
)