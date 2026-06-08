package com.example.core.data.repository

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
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.repository.MediaRepository
import com.example.core.domain.service.MediaService
import timber.log.Timber
import javax.inject.Inject

class MediaRepositoryImpl
    @Inject
    constructor(
        private val mediaService: MediaService,
    ) : MediaRepository {
        override suspend fun getSeasonalMedia(
            pageNumber: Int,
            perPage: Int,
            seasonYear: Int,
            season: MediaSeason,
            mediaType: MediaType,
        ): Result<Page<Media>> =
            mediaService
                .getSeasonalMediaList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    seasonYear = seasonYear,
                    season = season,
                    mediaType = mediaType,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get trending now media")
                }

        override suspend fun getAnimeByStatus(
            pageNumber: Int,
            perPage: Int,
            status: MediaListStatus,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            mediaService
                .getAnimeByStatusList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    status = status,
                    userId = userId,
                    sortBy = sortBy,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get anime by status")
                }

        override suspend fun getMangaByStatus(
            pageNumber: Int,
            perPage: Int,
            status: MediaListStatus,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            mediaService
                .getMangaByStatusList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    status = status,
                    userId = userId,
                    sortBy = sortBy,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get manga by status")
                }

        override suspend fun getUserListCollection(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            mediaService
                .getUserListCollection(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    mediaType = mediaType,
                    userId = userId,
                    sortBy = sortBy,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get user list collection")
                }

        override suspend fun getRecentlyUpdatedAnimeList(
            pageNumber: Int,
            perPage: Int,
            airingAtLesser: Int,
            airingAtGreater: Int,
        ): Result<Page<AiringSchedule>> =
            mediaService
                .getRecentlyUpdatedAnimeList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    airingAtLesser = airingAtLesser,
                    airingAtGreater = airingAtGreater,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get recently updated anime")
                }

        override suspend fun getTrendingNowMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
        ): Result<Page<Media>> =
            mediaService
                .getTrendingNowMediaList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    mediaType = mediaType,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get trending now media")
                }

        override suspend fun getPopularMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            mediaFormat: MediaFormat?,
            countryOfOrigin: String?,
        ): Result<Page<Media>> =
            mediaService
                .getPopularMediaList(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    mediaType = mediaType,
                    mediaFormat = mediaFormat,
                    countryOfOrigin = countryOfOrigin,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get trending now media")
                }

        override suspend fun getMediaById(
            id: Int,
            fetchFromNetwork: Boolean,
        ): Result<Media> =
            mediaService
                .getMediaById(
                    id = id,
                    fetchFromNetwork = fetchFromNetwork,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get media")
                }

        override suspend fun getMediaRecommendationsById(id: Int): Result<Page<Media>> =
            mediaService
                .getMediaRecommendationsById(
                    id = id,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get media")
                }

        override suspend fun getMediaThreads(
            pageNumber: Int,
            perPage: Int,
            mediaId: Int,
        ): Result<Page<Thread>> =
            mediaService
                .getMediaThreads(
                    pageNumber = pageNumber,
                    perPage = perPage,
                    mediaId = mediaId,
                ).onFailure { error ->
                    Timber.e(error, "Failed to get media threads")
                }

        override suspend fun getSearchMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            search: String?,
            season: MediaSeason?,
            seasonYear: Int?,
            format: MediaFormat?,
            status: MediaStatus?,
            countryOfOrigin: String?,
            genres: List<String>?,
            tags: List<String>?,
            sortBy: List<MediaSort>?,
        ): Result<Page<Media>> =
            mediaService.getSearchMedia(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = mediaType,
                search = search,
                season = season,
                seasonYear = seasonYear,
                format = format,
                status = status,
                countryOfOrigin = countryOfOrigin,
                genres = genres,
                tags = tags,
                sortBy = sortBy,
            )

        override suspend fun saveMediaListEntry(
            mediaId: Int,
            status: MediaListStatus?,
            score: Double?,
            progress: Int?,
            repeat: Int?,
            private: Boolean?,
            hiddenFromStatusLists: Boolean?,
            startedAt: FuzzyDate?,
            completedAt: FuzzyDate?,
            notes: String?,
        ): Result<MediaList> =
            mediaService
                .saveMediaListEntry(
                    mediaId = mediaId,
                    status = status,
                    score = score,
                    progress = progress,
                    repeat = repeat,
                    private = private,
                    hiddenFromStatusLists = hiddenFromStatusLists,
                    startedAt = startedAt,
                    completedAt = completedAt,
                    notes = notes,
                ).onFailure { error ->
                    Timber.e(error, "Failed to save media list entry")
                }

        override suspend fun deleteMediaListEntry(mediaListEntryId: Int): Result<Boolean> =
            mediaService
                .deleteMediaListEntry(
                    mediaListEntryId = mediaListEntryId,
                ).onFailure { error ->
                    Timber.e(error, "Failed to delete media list entry")
                }

        override suspend fun toggleFavourite(
            animeId: Int?,
            mangaId: Int?,
        ): Result<Boolean> =
            mediaService
                .toggleFavourite(
                    animeId = animeId,
                    mangaId = mangaId,
                ).onFailure { error ->
                    Timber.e(error, "Failed to toggle favourite")
                }

        override suspend fun clearCache() {
            mediaService.clearCache()
        }
    }
