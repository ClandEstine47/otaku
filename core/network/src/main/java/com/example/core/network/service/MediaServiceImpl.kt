package com.example.core.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.service.MediaService
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.TrendingNowQuery
import javax.inject.Inject

class MediaServiceImpl
    @Inject
    constructor(
        private val apolloClient: ApolloClient,
    ) : MediaService {
        override suspend fun getSeasonalMediaList(
            pageNumber: Int,
            perPage: Int,
            seasonYear: Int,
            season: MediaSeason,
            mediaType: MediaType,
        ): Result<List<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            SeasonalAnimeQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                seasonYear = Optional.present(seasonYear),
                                season = Optional.present(season.toNetworkMediaSeason()),
                                mediaType = Optional.present(mediaType.toNetworkMediaType()),
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
                        val seasonalMedia =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(seasonalMedia)
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getRecentlyUpdatedAnimeList(
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
                                ?.filter { it.media.type == MediaType.ANIME }
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

        override suspend fun getTrendingNowMediaList(
            pageNumber: Int,
            perPage: Int,
        ): Result<List<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            TrendingNowQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
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

        override suspend fun getPopularMediaList(pageNumber: Int): Result<List<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            SeasonalAnimeQuery(
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
                        val popularMedia =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(popularMedia)
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
