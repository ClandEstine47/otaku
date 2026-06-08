package com.example.core.network.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.api.CacheKey
import com.apollographql.apollo.cache.normalized.apolloStore
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.exception.ApolloException
import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaList
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.medialistcollection.MediaListSort
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.model.user.User
import com.example.core.domain.service.MediaService
import com.example.core.network.DeleteMediaListEntryMutation
import com.example.core.network.MediaQuery
import com.example.core.network.MediaRecommendationsQuery
import com.example.core.network.MediaSearchQuery
import com.example.core.network.MediaThreadsQuery
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SaveMediaListEntryMutation
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.ToggleFavouriteMutation
import com.example.core.network.TrendingNowQuery
import com.example.core.network.UserListCollectionQuery
import com.example.core.network.ViewerQuery
import com.example.core.network.fragment.MediaFavouriteImpl
import timber.log.Timber
import javax.inject.Inject

class MediaServiceImpl
    @Inject
    constructor(
        private val apolloClient: ApolloClient,
    ) : MediaService {
        override suspend fun getUserDetails(): Result<User> =
            try {
                val response =
                    apolloClient
                        .query(ViewerQuery())
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"

                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val user = response.data?.Viewer?.toDomainUser()

                        if (user != null) {
                            Result.success(user)
                        } else {
                            Result.failure(Exception("User data is null"))
                        }
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getSeasonalMediaList(
            pageNumber: Int,
            perPage: Int,
            seasonYear: Int,
            season: MediaSeason,
            mediaType: MediaType,
        ): Result<Page<Media>> =
            try {
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
                        ).execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val seasonalMedia =
                            response.data
                                ?.Page
                                ?.media
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

        override suspend fun getAnimeByStatusList(
            pageNumber: Int,
            perPage: Int,
            status: MediaListStatus,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            getMediaByStatusList(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = MediaType.ANIME,
                status = status,
                userId = userId,
                sortBy = sortBy,
            )

        override suspend fun getMangaByStatusList(
            pageNumber: Int,
            perPage: Int,
            status: MediaListStatus,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            getMediaByStatusList(
                pageNumber = pageNumber,
                perPage = perPage,
                mediaType = MediaType.MANGA,
                status = status,
                userId = userId,
                sortBy = sortBy,
            )

        override suspend fun getUserListCollection(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            try {
                val response =
                    apolloClient
                        .query(
                            UserListCollectionQuery(
                                userId = Optional.presentIfNotNull(userId),
                                type = Optional.present(mediaType.toNetworkMediaType()),
                                sort = Optional.present(sortBy?.map { it.toNetworkMediaListSort() } ?: listOf(MediaListSort.UPDATED_TIME_DESC.toNetworkMediaListSort())),
                                chunk = Optional.present(pageNumber),
                                perChunk = Optional.present(perPage),
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
                        val hasNextChunk = response.data?.MediaListCollection?.hasNextChunk
                        val pageInfo = PageInfo(currentPage = pageNumber, perPage = perPage, hasNextPage = hasNextChunk)
                        val mediaByCollection =
                            response.data
                                ?.MediaListCollection
                                ?.lists
                                ?.filterNotNull()
                                ?.flatMap { mediaListGroup ->
                                    mediaListGroup.entries
                                        ?.mapNotNull { entry ->
                                            val media = entry?.media?.toDomainMediaFromListCollection() ?: return@mapNotNull null
                                            media.copy(
                                                mediaListEntry =
                                                    MediaList(
                                                        id = entry.id,
                                                        status = entry.status?.toDomainMediaListStatus(),
                                                        score = entry.score ?: 0.0,
                                                        progress = entry.progress ?: 0,
                                                        progressVolumes = entry.progressVolumes,
                                                        repeat = entry.repeat ?: 0,
                                                        private = entry.private ?: false,
                                                        notes = entry.notes.orEmpty(),
                                                        hiddenFromStatusLists = entry.hiddenFromStatusLists ?: false,
                                                        advancedScores = entry.advancedScores,
                                                        media = media,
                                                    ),
                                            )
                                        }.orEmpty()
                                }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = mediaByCollection,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }

        private suspend fun getMediaByStatusList(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            status: MediaListStatus,
            userId: Int?,
            sortBy: List<MediaListSort>?,
        ): Result<Page<Media>> =
            try {
                val response =
                    apolloClient
                        .query(
                            UserListCollectionQuery(
                                userId = Optional.presentIfNotNull(userId),
                                type = Optional.present(mediaType.toNetworkMediaType()),
                                status = Optional.present(status.toNetworkMediaListStatus()),
                                sort = Optional.present(sortBy?.map { it.toNetworkMediaListSort() } ?: listOf(MediaListSort.UPDATED_TIME_DESC.toNetworkMediaListSort())),
                                chunk = Optional.present(pageNumber),
                                perChunk = Optional.present(perPage),
                            ),
                        ).fetchPolicy(FetchPolicy.NetworkFirst)
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val hasNextChunk = response.data?.MediaListCollection?.hasNextChunk
                        val pageInfo = PageInfo(currentPage = pageNumber, perPage = perPage, hasNextPage = hasNextChunk)
                        val mediaByStatus =
                            response.data
                                ?.MediaListCollection
                                ?.lists
                                ?.filterNotNull()
                                ?.flatMap { mediaListGroup ->
                                    mediaListGroup.entries
                                        ?.mapNotNull { entry ->
                                            val media = entry?.media?.toDomainMediaFromListCollection() ?: return@mapNotNull null
                                            media.copy(
                                                mediaListEntry =
                                                    MediaList(
                                                        id = entry.id,
                                                        status = entry.status?.toDomainMediaListStatus(),
                                                        score = entry.score ?: 0.0,
                                                        progress = entry.progress ?: 0,
                                                        progressVolumes = entry.progressVolumes,
                                                        repeat = entry.repeat ?: 0,
                                                        private = entry.private ?: false,
                                                        notes = entry.notes.orEmpty(),
                                                        hiddenFromStatusLists = entry.hiddenFromStatusLists ?: false,
                                                        advancedScores = entry.advancedScores,
                                                        media = media,
                                                    ),
                                            )
                                        }.orEmpty()
                                }
                                ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = pageInfo,
                                data = mediaByStatus,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getRecentlyUpdatedAnimeList(
            pageNumber: Int,
            perPage: Int,
            airingAtLesser: Int,
            airingAtGreater: Int,
        ): Result<Page<AiringSchedule>> =
            try {
                val response =
                    apolloClient
                        .query(
                            RecentlyUpdatedQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                airingAtLesser = airingAtLesser,
                                airingAtGreater = Optional.present(airingAtGreater),
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
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val airingSchedules =
                            response.data
                                ?.Page
                                ?.airingSchedules
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

        override suspend fun getTrendingNowMediaList(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
        ): Result<Page<Media>> =
            try {
                val response =
                    apolloClient
                        .query(
                            TrendingNowQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                mediaType = Optional.present(mediaType.toNetworkMediaType()),
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
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val trendingNow =
                            response.data
                                ?.Page
                                ?.media
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

        override suspend fun getPopularMediaList(
            pageNumber: Int,
            perPage: Int,
            mediaType: MediaType,
            mediaFormat: MediaFormat?,
            countryOfOrigin: String?,
        ): Result<Page<Media>> =
            try {
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
                        ).execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val popularMedia =
                            response.data
                                ?.Page
                                ?.media
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

        override suspend fun getMediaById(
            id: Int,
            fetchFromNetwork: Boolean,
        ): Result<Media> =
            try {
                val response =
                    apolloClient
                        .query(
                            MediaQuery(
                                id = id,
                            ),
                        ).fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
                        .execute()

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

        override suspend fun getMediaRecommendationsById(id: Int): Result<Page<Media>> =
            try {
                val response =
                    apolloClient
                        .query(
                            MediaRecommendationsQuery(
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
                        val mediaRecommendations =
                            response.data
                                ?.Media
                                ?.recommendations
                                ?.toDomainMediaRecommendations() ?: emptyList()
                        Result.success(
                            Page(
                                pageInfo = PageInfo(),
                                data = mediaRecommendations,
                            ),
                        )
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getMediaThreads(
            pageNumber: Int,
            perPage: Int,
            mediaId: Int,
        ): Result<Page<Thread>> =
            try {
                val response =
                    apolloClient
                        .query(
                            MediaThreadsQuery(
                                page = pageNumber,
                                perPage = Optional.present(perPage),
                                mediaCategoryId = Optional.present(mediaId),
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
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val threads =
                            response.data
                                ?.Page
                                ?.threads
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
            sortBy: List<MediaSort>?,
        ): Result<Page<Media>> =
            try {
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
                                sort = Optional.presentIfNotNull(sortBy?.map { it.toNetworkMediaSort() }),
                            ),
                        ).fetchPolicy(FetchPolicy.NetworkOnly)
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val pageInfo =
                            response.data
                                ?.Page
                                ?.pageInfo
                                ?.toDomainPageInfo()
                        val mediaSearch =
                            response.data
                                ?.Page
                                ?.media
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

        override suspend fun saveMediaListEntry(
            mediaId: Int,
            status: MediaListStatus?,
            score: Double?,
            progress: Int?,
            repeat: Int?,
            private: Boolean?,
            hiddenFromStatusLists: Boolean?,
            startedAt: FuzzyDate?,
            completedAt: FuzzyDate?,
            notes: String?,
        ): Result<MediaList> =
            try {
                val response =
                    apolloClient
                        .mutation(
                            SaveMediaListEntryMutation(
                                mediaId = mediaId,
                                status = Optional.presentIfNotNull(status?.toNetworkMediaListStatus()),
                                score = Optional.presentIfNotNull(score),
                                progress = Optional.presentIfNotNull(progress),
                                repeat = Optional.presentIfNotNull(repeat),
                                private = Optional.presentIfNotNull(private),
                                hiddenFromStatusLists = Optional.presentIfNotNull(hiddenFromStatusLists),
                                startedAt = Optional.present(startedAt?.toNetworkFuzzyDateInput()),
                                completedAt = Optional.present(completedAt?.toNetworkFuzzyDateInput()),
                                notes = Optional.presentIfNotNull(notes),
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
                        val entry = response.data?.SaveMediaListEntry?.toDomainMediaList()
                        if (entry != null) {
                            Result.success(entry)
                        } else {
                            Result.failure(Exception("Saved entry data is null"))
                        }
                    }
                }
            } catch (e: ApolloException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun deleteMediaListEntry(mediaListEntryId: Int): Result<Boolean> =
            try {
                val response =
                    apolloClient
                        .mutation(DeleteMediaListEntryMutation(mediaListEntryId = Optional.present(mediaListEntryId)))
                        .execute()

                when {
                    response.hasErrors() -> {
                        val errorMessage =
                            response.errors?.joinToString("; ") { it.message }
                                ?: "Unknown GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }

                    else -> {
                        val deleted = response.data?.DeleteMediaListEntry?.deleted ?: false
                        Result.success(deleted)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun toggleFavourite(
            animeId: Int?,
            mangaId: Int?,
        ): Result<Boolean> =
            try {
                val mediaId = animeId ?: mangaId ?: throw Exception("Media ID is required")
                val response =
                    apolloClient
                        .mutation(
                            ToggleFavouriteMutation(
                                animeId = Optional.presentIfNotNull(animeId),
                                mangaId = Optional.presentIfNotNull(mangaId),
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
                        // Update cache
                        val cacheKey = CacheKey("Media", mediaId.toString())
                        try {
                            val existingFragment =
                                apolloClient.apolloStore.readFragment(MediaFavouriteImpl(), cacheKey)
                            val updatedFragment =
                                existingFragment.copy(isFavourite = !existingFragment.isFavourite)
                            apolloClient.apolloStore.writeFragment(
                                MediaFavouriteImpl(),
                                cacheKey,
                                updatedFragment,
                            )
                        } catch (e: Exception) {
                            Timber.d("Cache update failed for $cacheKey: ${e.message}")
                        }
                        Result.success(true)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
