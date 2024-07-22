package com.example.core.domain.repository

import com.example.core.domain.model.airing.AiringSchedule

interface MediaRepository {

    suspend fun getRecentlyUpdatedMedia(
        pageNumber: Int,
        airingTimeInMs: Int
    ): List<AiringSchedule>
}