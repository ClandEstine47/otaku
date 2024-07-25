package com.example.core.data.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.repository.MediaRepository
import com.example.core.domain.service.MediaService
import timber.log.Timber
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaService: MediaService
): MediaRepository {

    override suspend fun getRecentlyUpdatedMedia(
        pageNumber: Int,
        airingTimeInMs: Int
    ): Result<List<AiringSchedule>> {

        return mediaService.getRecentlyUpdatedMediaList(
            pageNumber = pageNumber,
            airingTimeInMs = airingTimeInMs
        ).onFailure { error ->
            Timber.e(error, "Failed to get recently updated media")
        }
    }

    override suspend fun getTrendingNowMedia(
        pageNumber: Int
    ): Result<List<Media>> {

        return mediaService.getTrendingNowMediaList(
            pageNumber = pageNumber
        ).onFailure { error ->
            Timber.e(error, "Failed to get trending now media")
        }
    }
}