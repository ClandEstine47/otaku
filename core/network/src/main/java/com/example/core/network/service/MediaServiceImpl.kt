package com.example.core.network.service

import com.apollographql.apollo3.ApolloClient
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.service.MediaService
import com.example.core.network.RecentlyUpdatedQuery
import javax.inject.Inject

class MediaServiceImpl @Inject constructor(
    private val apolloClient: ApolloClient
): MediaService {
    override suspend fun getTrendingMediaList(pageNumber: Int, airingTimeInMs: Int): List<AiringSchedule> {
        return apolloClient
            .query(RecentlyUpdatedQuery(page = pageNumber, airingAtLesser = airingTimeInMs))
            .execute()
            .data
            ?.Page
            ?.airingSchedules
            ?.mapNotNull { it?.toRecentlyUpdatedMedia() }
            ?: emptyList()
    }
}