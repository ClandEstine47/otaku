package com.example.core.domain.model.user

import com.example.core.domain.model.media.MediaListOptions

data class User(
    val id: Int = 0,
    val name: String? = "",
    val about: String = "",
    val avatar: UserAvatar = UserAvatar(),
    val bannerImage: String = "",
    var isFollowing: Boolean = false,
    val isFollower: Boolean = false,
    val isBlocked: Boolean = false,
    val options: UserOptions = UserOptions(),
    val mediaListOptions: MediaListOptions = MediaListOptions(),
    var favourites: Favourites = Favourites(),
    val statistics: UserStatisticTypes = UserStatisticTypes(),
    val unreadNotificationCount: Int = 0,
    val siteUrl: String = "",
    val donatorTier: Int = 0,
    val donatorBadge: String = "",
    val moderatorRoles: List<ModRole> = listOf(),
    val createdAt: Int = 0,
)
