package com.example.core.navigation

import kotlinx.serialization.Serializable

sealed interface OtakuScreen {
    @Serializable
    data object AnimeTab : OtakuScreen

    @Serializable
    data object MangaTab : OtakuScreen
}
