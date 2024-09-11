package com.example.core.domain.repository

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaType

interface MediaRepository {
    suspend fun getSeasonalMedia(
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

    suspend fun getTrendingNowMedia(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
    ): Result<List<Media>>

    suspend fun getPopularMedia(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        mediaFormat: MediaFormat? = null,
        countryOfOrigin: String? = null,
    ): Result<List<Media>>
}
