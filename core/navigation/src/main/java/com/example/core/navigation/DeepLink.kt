package com.example.core.navigation

data class DeepLink(
    val type: Type,
    val id: String,
) {
    enum class Type {
        ANIME,
        MANGA,
    }
}
