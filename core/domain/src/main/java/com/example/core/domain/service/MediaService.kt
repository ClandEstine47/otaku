package com.example.core.domain.service

import com.example.core.domain.model.Page
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.thread.Thread

interface MediaService {
    suspend fun getSeasonalMediaList(
        pageNumber: Int,
        perPage: Int,
        seasonYear: Int,
        season: MediaSeason,
        mediaType: MediaType,
    ): Result<Page<Media>>

    suspend fun getRecentlyUpdatedAnimeList(
        pageNumber: Int,
        perPage: Int,
        airingAtLesser: Int,
        airingAtGreater: Int,
    ): Result<Page<AiringSchedule>>

    suspend fun getTrendingNowMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
    ): Result<Page<Media>>

    suspend fun getPopularMediaList(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        mediaFormat: MediaFormat?,
        countryOfOrigin: String?,
    ): Result<Page<Media>>

    suspend fun getMediaById(
        id: Int,
    ): Result<Media>

    suspend fun getMediaThreads(
        pageNumber: Int,
        perPage: Int,
        mediaId: Int,
    ): Result<Page<Thread>>

    suspend fun getSearchMedia(
        pageNumber: Int,
        perPage: Int,
        mediaType: MediaType,
        search: String? = null,
        season: MediaSeason? = null,
        seasonYear: Int? = null,
        format: MediaFormat? = null,
        status: MediaStatus? = null,
        countryOfOrigin: String? = null,
        genres: List<String>? = null,
        tags: List<String>? = null,
        sortBy: List<MediaSort>? = null,
    ): Result<Page<Media>>
}
