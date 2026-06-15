package com.example.core.domain.service

import com.example.core.domain.model.Page
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaList
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.medialistcollection.MediaListSort
import com.example.core.domain.model.notification.Notification
import com.example.core.domain.model.notification.NotificationType
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.model.user.User

interface MediaService {
    suspend fun getUserDetails(): Result<User>

    suspend fun getSeasonalMediaList(
        pageNumber: Int,
        perPage: Int,
        seasonYear: Int,
        season: MediaSeason,
        mediaType: MediaType,
    ): Result<Page<Media>>

    suspend fun getAnimeByStatusList(
        pageNumber: Int,
        perPage: Int,
        status: MediaListStatus,
        userId: Int? = null,
        sortBy: List<MediaListSort>? = null,
    ): Result<Page<Media>>

    suspend fun getMangaByStatusList(
        pageNumber: Int,
        perPage: Int,
        status: MediaListStatus,
        userId: Int? = null,
        sortBy: List<MediaListSort>? = null,
    ): Result<Page<Media>>

    suspend fun getUserListCollection(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        userId: Int? = null,
        sortBy: List<MediaListSort>? = null,
    ): Result<Page<Media>>

    suspend fun getRecentlyUpdatedAnimeList(
        pageNumber: Int,
        perPage: Int,
        airingAtLesser: Int,
        airingAtGreater: Int,
    ): Result<Page<AiringSchedule>>

    suspend fun getTrendingNowMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
    ): Result<Page<Media>>

    suspend fun getPopularMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        mediaFormat: MediaFormat?,
        countryOfOrigin: String?,
    ): Result<Page<Media>>

    suspend fun getMediaById(
        id: Int,
        fetchFromNetwork: Boolean,
    ): Result<Media>

    suspend fun getMediaRecommendationsById(
        id: Int,
    ): Result<Page<Media>>

    suspend fun getMediaThreads(
        pageNumber: Int,
        perPage: Int,
        mediaId: Int,
    ): Result<Page<Thread>>

    suspend fun getSearchMedia(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        search: String? = null,
        season: MediaSeason? = null,
        seasonYear: Int? = null,
        format: MediaFormat? = null,
        status: MediaStatus? = null,
        countryOfOrigin: String? = null,
        genres: List<String>? = null,
        tags: List<String>? = null,
        sortBy: List<MediaSort>? = null,
    ): Result<Page<Media>>

    suspend fun saveMediaListEntry(
        mediaId: Int,
        status: MediaListStatus? = null,
        score: Double? = null,
        progress: Int? = null,
        repeat: Int? = null,
        private: Boolean? = null,
        hiddenFromStatusLists: Boolean? = null,
        startedAt: FuzzyDate? = null,
        completedAt: FuzzyDate? = null,
        notes: String? = null,
    ): Result<MediaList>

    suspend fun deleteMediaListEntry(
        mediaListEntryId: Int,
    ): Result<Boolean>

    suspend fun toggleFavourite(
        animeId: Int? = null,
        mangaId: Int? = null,
    ): Result<Boolean>

    suspend fun getNotifications(
        pageNumber: Int,
        perPage: Int,
        resetCount: Boolean,
        types: List<NotificationType>? = null,
    ): Result<Page<Notification>>

    suspend fun clearCache()
}
