package com.example.core.domain.model.studio

import com.example.core.domain.model.media.MediaConnection

data class Studio(
    val id: Int = 0,
    val name: String = "",
    val isAnimationStudio: Boolean = false,
    val media: MediaConnection = MediaConnection(),
    val siteUrl: String = "",
    val isFavourite: Boolean = false,
    val favourites: Int = 0
)