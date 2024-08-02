package com.example.core.domain.service

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaSeason

interface MediaService {
    suspend fun getSeasonalMediaList(
        pageNumber: Int,
        seasonYear: Int,
        season: MediaSeason,
    ): Result<List<Media>>

    suspend fun getRecentlyUpdatedMediaList(
        pageNumber: Int,
        airingTimeInMs: Int,
    ): Result<List<AiringSchedule>>

    suspend fun getTrendingNowMediaList(pageNumber: Int): Result<List<Media>>
}
