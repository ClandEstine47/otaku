package com.example.core.domain.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media

interface MediaRepository {
    suspend fun getRecentlyUpdatedMedia(
        pageNumber: Int,
        airingTimeInMs: Int,
    ): Result<List<AiringSchedule>>

    suspend fun getTrendingNowMedia(pageNumber: Int): Result<List<Media>>
}
