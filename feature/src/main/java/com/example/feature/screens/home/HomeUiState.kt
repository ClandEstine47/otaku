package com.example.feature.screens.home

import com.example.core.domain.model.media.Media
import com.example.core.domain.model.user.User

data class HomeUiState(
    val currentAnimeMedia: List<Media>? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val user: User = User(),
)
