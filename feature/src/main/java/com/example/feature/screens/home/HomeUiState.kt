package com.example.feature.screens.home

import com.example.core.domain.model.user.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val user: User = User(),
)
