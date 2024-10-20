package com.example.core.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.core.domain.model.Page
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.service.MediaService
import com.example.core.network.MediaQuery
import com.example.core.network.MediaSearchQuery
import com.example.core.network.MediaThreadsQuery
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
        ): Result<Page<Media>> {
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val seasonalMedia =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = seasonalMedia,
                            ),
                        )
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
            perPage: Int,
            airingAtLesser: Int,
            airingAtGreater: Int,
        ): Result<Page<AiringSchedule>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            RecentlyUpdatedQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                airingAtLesser = airingAtLesser,
                                airingAtGreater = Optional.present(airingAtGreater),
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val airingSchedules =
                            response.data?.Page?.airingSchedules
                                ?.mapNotNull { it?.toRecentlyUpdatedMedia() }
                                ?.filter { it.media.type == MediaType.ANIME }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = airingSchedules,
                            ),
                        )
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
            mediaType: MediaType,
        ): Result<Page<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            TrendingNowQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val trendingNow =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = trendingNow,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getPopularMediaList(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            mediaFormat: MediaFormat?,
            countryOfOrigin: String?,
        ): Result<Page<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            SeasonalAnimeQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                mediaType = Optional.present(mediaType.toNetworkMediaType()),
                                mediaFormat = Optional.presentIfNotNull(mediaFormat?.toNetworkMediaFormat()),
                                countryOfOrigin = Optional.presentIfNotNull(countryOfOrigin),
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val popularMedia =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = popularMedia,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getMediaById(id: Int): Result<Media> {
            return try {
                val response =
                    apolloClient
                        .query(
                            MediaQuery(
                                id = id,
                            ),
                        ).execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val media =
                            response.data?.Media?.toDomainMedia() ?: Media()
                        Result.success(
                            media,
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getMediaThreads(
            pageNumber: Int,
            perPage: Int,
            mediaId: Int,
        ): Result<Page<Thread>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            MediaThreadsQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                mediaCategoryId = Optional.present(mediaId),
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val threads =
                            response.data?.Page?.threads
                                ?.mapNotNull { it?.toDomainThread() }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = threads,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getSearchMedia(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            search: String?,
            season: MediaSeason?,
            seasonYear: Int?,
            format: MediaFormat?,
            status: MediaStatus?,
            countryOfOrigin: String?,
            genres: List<String>?,
            tags: List<String>?,
        ): Result<Page<Media>> {
            return try {
                val response =
                    apolloClient
                        .query(
                            MediaSearchQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                type = Optional.present(mediaType.toNetworkMediaType()),
                                search = Optional.presentIfNotNull(search),
                                format = Optional.presentIfNotNull(format?.toNetworkMediaFormat()),
                                status = Optional.presentIfNotNull(status?.toNetworkMediaStatus()),
                                countryOfOrigin = Optional.presentIfNotNull(countryOfOrigin),
                                seasonYear = Optional.presentIfNotNull(seasonYear),
                                season = Optional.presentIfNotNull(season?.toNetworkMediaSeason()),
                                genres = Optional.presentIfNotNull(genres),
                                tags = Optional.presentIfNotNull(tags),
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
                        val pageInfo = response.data?.Page?.pageInfo?.toDomainPageInfo()
                        val mediaSearch =
                            response.data?.Page?.media
                                ?.mapNotNull { it?.toDomainMedia() }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = mediaSearch,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
