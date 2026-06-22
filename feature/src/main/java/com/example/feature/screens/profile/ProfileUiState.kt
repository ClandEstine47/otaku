package com.example.feature.screens.profile

import com.example.core.domain.model.user.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
