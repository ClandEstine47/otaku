package com.example.core.domain.service

import com.example.core.domain.model.airing.AiringSchedule

interface MediaService {

    suspend fun getTrendingMediaList(pageNumber: Int, airingTimeInMs: Int): List<AiringSchedule>
}