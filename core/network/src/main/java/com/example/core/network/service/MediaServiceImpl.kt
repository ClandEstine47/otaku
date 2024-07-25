package com.example.core.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.service.MediaService
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.TrendingNowQuery
import javax.inject.Inject

class MediaServiceImpl
    @Inject
    constructor(
        private val apolloClient: ApolloClient,
    ) : MediaService {
        override suspend fun getRecentlyUpdatedMediaList(
            pageNumber: Int,
            airingTimeInMs: Int,
        ): Result<List<AiringSchedule>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            RecentlyUpdatedQuery(
                                page = pageNumber,
                                airingAtLesser = airingTimeInMs,
                            ),
                        )
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val airingSchedules =
                            response.data?.Page?.airingSchedules
                                ?.mapNotNull { it?.toRecentlyUpdatedMedia() }
                                ?: emptyList()
                        Result.success(airingSchedules)
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getTrendingNowMediaList(pageNumber: Int): Result<List<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            TrendingNowQuery(
                                page = pageNumber,
                            ),
                        )
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val trendingNow =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(trendingNow)
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
