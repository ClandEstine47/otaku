package com.example.core.domain.service

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaType

interface MediaService {
    suspend fun getSeasonalMediaList(
        pageNumber: Int,
        perPage: Int,
        seasonYear: Int,
        season: MediaSeason,
        mediaType: MediaType,
    ): Result<List<Media>>

    suspend fun getRecentlyUpdatedAnimeList(
        pageNumber: Int,
        perPage: Int,
        airingAtLesser: Int,
        airingAtGreater: Int,
    ): Result<List<AiringSchedule>>

    suspend fun getTrendingNowMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
    ): Result<List<Media>>

    suspend fun getPopularMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        mediaFormat: MediaFormat?,
        countryOfOrigin: String?,
    ): Result<List<Media>>
}
