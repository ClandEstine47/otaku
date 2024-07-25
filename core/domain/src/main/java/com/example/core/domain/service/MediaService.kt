package com.example.core.domain.service

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media

interface MediaService {

    suspend fun getRecentlyUpdatedMediaList(pageNumber: Int, airingTimeInMs: Int): Result<List<AiringSchedule>>

    suspend fun getTrendingNowMediaList(pageNumber: Int): Result<List<Media>>
}