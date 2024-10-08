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
    data object MediaSearch : OtakuScreen

    @Serializable
    data class MediaList(
        val titleId: Int,
        val mediaType: MediaType,
        val contentType: MediaListContentType,
    ) : OtakuScreen

    @Serializable
    data class MediaDetail(
        val id: Int,
        val mediaType: MediaType,
    ) : OtakuScreen
}
