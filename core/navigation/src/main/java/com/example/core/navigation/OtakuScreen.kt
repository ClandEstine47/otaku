package com.example.core.navigation

import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.media.MediaType
import kotlinx.serialization.Serializable

sealed interface OtakuScreen {
    @Serializable
    data object AnimeTab : OtakuScreen

    @Serializable
    data object MangaTab : OtakuScreen

    @Serializable
    data object HomeTab : OtakuScreen

    @Serializable
    data class MediaSearch(
        val mediaType: MediaType,
    ) : OtakuScreen

    @Serializable
    data class MediaList(
        val titleId: Int,
        val mediaId: Int?,
        val mediaType: MediaType,
        val contentType: MediaListContentType,
        val userId: Int? = null,
        val showStatusTabs: Boolean = false,
    ) : OtakuScreen

    @Serializable
    data class MediaDetail(
        val id: Int,
        val mediaType: MediaType,
    ) : OtakuScreen

    @Serializable
    data object Notifications : OtakuScreen
}
