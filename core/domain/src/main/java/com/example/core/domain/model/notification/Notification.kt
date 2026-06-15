package com.example.core.domain.model.notification

import com.example.core.domain.model.media.Media

sealed interface Notification {
    val id: Int
    val type: NotificationType
    val createdAt: Int

    data class Airing(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
        val contexts: List<String?>?,
        val animeId: Int,
        val episode: Int,
        val media: Media,
    ) : Notification

    data class RelatedMediaAddition(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
        val context: String?,
        val mediaId: Int,
        val media: Media,
    ) : Notification

    data class MediaDataChange(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
        val context: String?,
        val mediaId: Int,
        val reason: String?,
        val media: Media,
    ) : Notification

    data class MediaMerge(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
        val context: String?,
        val mediaId: Int,
        val reason: String?,
        val media: Media,
    ) : Notification

    data class MediaDeletion(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
        val context: String?,
        val reason: String?,
        val deletedMediaTitle: String?,
    ) : Notification

    data class Unknown(
        override val id: Int,
        override val type: NotificationType,
        override val createdAt: Int,
    ) : Notification
}
