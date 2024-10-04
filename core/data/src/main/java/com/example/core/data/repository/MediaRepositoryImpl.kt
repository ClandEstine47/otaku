package com.example.core.data.repository

import com.example.core.domain.model.Page
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaType
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
        ): Result<Page<Media>> {
            return mediaService.getSeasonalMediaList(
                pageNumber = pageNumber,
                perPage = perPage,
                seasonYear = seasonYear,
                season = season,
                mediaType = mediaType,
            ).onFailure { error ->
                Timber.e(error, "Failed to get trending now media")
            }
        }

        override suspend fun getRecentlyUpdatedAnimeList(
            pageNumber: Int,
            perPage: Int,
            airingAtLesser: Int,
            airingAtGreater: Int,
        ): Result<Page<AiringSchedule>> {
            return mediaService.getRecentlyUpdatedAnimeList(
                pageNumber = pageNumber,
                perPage = perPage,
                airingAtLesser = airingAtLesser,
                airingAtGreater = airingAtGreater,
            ).onFailure { error ->
                Timber.e(error, "Failed to get recently updated anime")
            }
        }

        override suspend fun getTrendingNowMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
        ): Result<Page<Media>> {
            return mediaService.getTrendingNowMediaList(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = mediaType,
            ).onFailure { error ->
                Timber.e(error, "Failed to get trending now media")
            }
        }

        override suspend fun getPopularMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            mediaFormat: MediaFormat?,
            countryOfOrigin: String?,
        ): Result<Page<Media>> {
            return mediaService.getPopularMediaList(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = mediaType,
                mediaFormat = mediaFormat,
                countryOfOrigin = countryOfOrigin,
            ).onFailure { error ->
                Timber.e(error, "Failed to get trending now media")
            }
        }

        override suspend fun getMediaById(id: Int): Result<Media> {
            return mediaService.getMediaById(
                id = id,
            ).onFailure { error ->
                Timber.e(error, "Failed to get media")
            }
        }

        override suspend fun getMediaThreads(
            pageNumber: Int,
            perPage: Int,
            mediaId: Int,
        ): Result<Page<Thread>> {
            return mediaService.getMediaThreads(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaId = mediaId,
            ).onFailure { error ->
                Timber.e(error, "Failed to get media threads")
            }
        }
    }
