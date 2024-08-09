package com.example.core.domain.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaSeason

interface MediaRepository {
    suspend fun getSeasonalMedia(
        pageNumber: Int,
        seasonYear: Int,
        season: MediaSeason,
    ): Result<List<Media>>

    suspend fun getRecentlyUpdatedMedia(
        pageNumber: Int,
        airingTimeInMs: Int,
    ): Result<List<AiringSchedule>>

    suspend fun getTrendingNowMedia(pageNumber: Int): Result<List<Media>>

    suspend fun getPopularMedia(pageNumber: Int): Result<List<Media>>
}
