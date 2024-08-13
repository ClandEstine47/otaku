package com.example.core.data.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaType
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
        ): Result<List<Media>> {
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
            airingTimeInMs: Int,
        ): Result<List<AiringSchedule>> {
            return mediaService.getRecentlyUpdatedAnimeList(
                pageNumber = pageNumber,
                airingTimeInMs = airingTimeInMs,
            ).onFailure { error ->
                Timber.e(error, "Failed to get recently updated anime")
            }
        }

        override suspend fun getTrendingNowMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
        ): Result<List<Media>> {
            return mediaService.getTrendingNowMediaList(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = mediaType,
            ).onFailure { error ->
                Timber.e(error, "Failed to get trending now media")
            }
        }

        override suspend fun getPopularMedia(pageNumber: Int): Result<List<Media>> {
            return mediaService.getPopularMediaList(
                pageNumber = pageNumber,
            ).onFailure { error ->
                Timber.e(error, "Failed to get trending now media")
            }
        }
    }
