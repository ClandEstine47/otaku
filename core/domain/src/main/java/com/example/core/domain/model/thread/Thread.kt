package com.example.core.domain.model.thread

import com.example.core.domain.model.user.User

data class Thread(
    val id: Int? = 0,
    val title: String? = "",
    val body: String? = "",
    val isLiked: Boolean? = false,
    val isLocked: Boolean? = false,
    val isSubscribed: Boolean? = false,
    val likeCount: Int? = 0,
    val replyCount: Int? = 0,
    val viewCount: Int? = 0,
    val user: User? = User(),
    val createdAt: Int? = 0,
)
