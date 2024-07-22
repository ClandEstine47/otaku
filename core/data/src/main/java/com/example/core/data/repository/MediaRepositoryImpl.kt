package com.example.core.data.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.repository.MediaRepository
import com.example.core.domain.service.MediaService
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaService: MediaService
): MediaRepository {

    override suspend fun getRecentlyUpdatedMedia(
        pageNumber: Int,
        airingTimeInMs: Int
    ): List<AiringSchedule> {
        return mediaService.getTrendingMediaList(
            pageNumber = pageNumber,
            airingTimeInMs = airingTimeInMs
        )
    }
}