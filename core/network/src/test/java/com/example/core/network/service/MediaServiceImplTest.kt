package com.example.core.network.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.ApolloRequest
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.apollographql.apollo.network.NetworkTransport
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestResponse
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.network.DeleteMediaListEntryMutation
import com.example.core.network.MediaQuery
import com.example.core.network.MediaRecommendationsQuery
import com.example.core.network.MediaSearchQuery
import com.example.core.network.MediaThreadsQuery
import com.example.core.network.NotificationsQuery
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SaveMediaListEntryMutation
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.ToggleFavouriteMutation
import com.example.core.network.TrendingNowQuery
import com.example.core.network.UserListCollectionQuery
import com.example.core.network.ViewerQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.example.core.network.type.MediaFormat as NetworkMediaFormat
import com.example.core.network.type.MediaListSort as NetworkMediaListSort
import com.example.core.network.type.MediaListStatus as NetworkMediaListStatus
import com.example.core.network.type.MediaSeason as NetworkMediaSeason
import com.example.core.network.type.MediaStatus as NetworkMediaStatus
import com.example.core.network.type.MediaType as NetworkMediaType
import com.example.core.network.type.NotificationType as NetworkNotificationType

@OptIn(ApolloExperimental::class)
@RunWith(JUnit4::class)
class MediaServiceImplTest {
    private lateinit var testClient: ApolloClient
    private lateinit var mediaService: MediaServiceImpl

    private val defaultParams =
        TestParams(
            pageNumber = 1,
            perPage = 20,
            seasonYear = 2024,
            season = MediaSeason.SPRING,
            mediaType = MediaType.ANIME,
            mediaFormat = MediaFormat.TV,
            mediaStatus = MediaStatus.FINISHED,
            airingAtLesser = 1698710400,
            airingAtGreater = 1698624000,
            countryOfOrigin = "JP",
            mediaId = 123,
            genres = null,
            tags = null,
            sort = listOf(MediaSort.POPULARITY),
            searchQuery = "One Piece",
        )

    @Before
    fun setup() {
        testClient =
            ApolloClient
                .Builder()
                .networkTransport(QueueTestNetworkTransport())
                .build()
        mediaService = MediaServiceImpl(testClient)
    }

    @Test
    fun `getSeasonalMediaList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                SeasonalAnimeQuery.Data(
                    Page =
                        SeasonalAnimeQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                SeasonalAnimeQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    SeasonalAnimeQuery.Medium(
                                        __typename = "Media",
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            SeasonalAnimeQuery.NextAiringEpisode(
                                                __typename = "AiringSchedule",
                                                episode = 12,
                                            ),
                                        isAdult = false,
                                        type = NetworkMediaType.ANIME,
                                        description = "",
                                        genres = emptyList(),
                                        meanScore = 90,
                                        isFavourite = false,
                                        format = NetworkMediaFormat.TV,
                                        bannerImage = "",
                                        countryOfOrigin = "JP",
                                        coverImage =
                                            SeasonalAnimeQuery.CoverImage(
                                                __typename = "MediaCoverImage",
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            SeasonalAnimeQuery.Title(
                                                __typename = "MediaTitle",
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            SeasonalAnimeQuery.MediaListEntry(
                                                __typename = "MediaList",
                                                progress = 12,
                                                private = false,
                                                score = 10.0,
                                                status = null,
                                            ),
                                    ),
                                ),
                        ),
                )

            // Create the query with test parameters
            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.present(defaultParams.seasonYear),
                    season = Optional.present(defaultParams.season.toNetworkMediaSeason()),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    assertEquals("Test Anime", title.romaji)
                    assertEquals("Test Anime EN", title.english)
                    assertEquals(123, idAniList)
                    assertEquals(12, episodes)
                    assertEquals(90, meanScore)
                    assertEquals(MediaFormat.TV, format)
                }
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getSeasonalMediaList returns empty list when API returns null media list`() =
        runTest {
            // Given
            val testData =
                SeasonalAnimeQuery.Data(
                    Page =
                        SeasonalAnimeQuery.Page(
                            __typename = "Page",
                            pageInfo = null,
                            media = null,
                        ),
                )

            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.present(defaultParams.seasonYear),
                    season = Optional.present(defaultParams.season.toNetworkMediaSeason()),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                assertNull(page.pageInfo)
            }
        }

    @Test
    fun `getSeasonalMediaList returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.present(defaultParams.seasonYear),
                    season = Optional.present(defaultParams.season.toNetworkMediaSeason()),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getSeasonalMediaList returns failure result when ApolloException occurs`() =
        runTest {
            // Given
            SeasonalAnimeQuery(
                page = defaultParams.pageNumber,
                perPage = Optional.present(defaultParams.perPage),
                seasonYear = Optional.present(defaultParams.seasonYear),
                season = Optional.present(defaultParams.season.toNetworkMediaSeason()),
                mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
            )

            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network error", exception.message)
            }
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                RecentlyUpdatedQuery.Data(
                    Page =
                        RecentlyUpdatedQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                RecentlyUpdatedQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            airingSchedules =
                                listOf(
                                    RecentlyUpdatedQuery.AiringSchedule(
                                        __typename = "AiringSchedule",
                                        episode = 5,
                                        airingAt = 1698624100,
                                        media =
                                            RecentlyUpdatedQuery.Media(
                                                __typename = "Media",
                                                id = 456,
                                                idMal = 789,
                                                title =
                                                    RecentlyUpdatedQuery.Title(
                                                        __typename = "MediaTitle",
                                                        romaji = "Test Anime",
                                                        english = "Test Anime EN",
                                                        userPreferred = "Test Anime EN",
                                                    ),
                                                type = NetworkMediaType.ANIME,
                                                format = NetworkMediaFormat.TV,
                                                status = NetworkMediaStatus.RELEASING,
                                                chapters = 10,
                                                episodes = 12,
                                                countryOfOrigin = "JP",
                                                bannerImage = "banner.jpg",
                                                coverImage =
                                                    RecentlyUpdatedQuery.CoverImage(
                                                        __typename = "MediaCoverImage",
                                                        large = "cover_large.jpg",
                                                        extraLarge = "cover_xl.jpg",
                                                    ),
                                                genres = listOf("Action", "Adventure"),
                                                meanScore = 85,
                                                isAdult = false,
                                                isFavourite = false,
                                                mediaListEntry = null,
                                                nextAiringEpisode =
                                                    RecentlyUpdatedQuery.NextAiringEpisode(
                                                        __typename = "AiringSchedule",
                                                        episode = 6,
                                                    ),
                                            ),
                                    ),
                                ),
                        ),
                )

            val query =
                RecentlyUpdatedQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = Optional.present(defaultParams.airingAtGreater),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    with(media) {
                        assertEquals(789, idMal)
                        assertEquals("Test Anime", title.romaji)
                        assertEquals("Test Anime EN", title.english)
                        assertEquals(MediaType.ANIME, type)
                        assertEquals(MediaFormat.TV, format)
                        assertEquals(85, meanScore)
                        assertEquals(listOf("Action", "Adventure"), genres)
                    }
                }

                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns empty list when API returns null schedules`() =
        runTest {
            // Given
            val testData =
                RecentlyUpdatedQuery.Data(
                    Page =
                        RecentlyUpdatedQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                RecentlyUpdatedQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 0,
                                    currentPage = 1,
                                    hasNextPage = false,
                                ),
                            airingSchedules = null,
                        ),
                )

            val query =
                RecentlyUpdatedQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = Optional.present(defaultParams.airingAtGreater),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(0, total)
                    assertEquals(false, hasNextPage)
                }
            }
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                RecentlyUpdatedQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = Optional.present(defaultParams.airingAtGreater),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns failure result when network error occurs`() =
        runTest {
            // Given
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network connection failed")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network connection failed", exception.message)
            }
        }

    @Test
    fun `getTrendingNowMediaList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                TrendingNowQuery.Data(
                    Page =
                        TrendingNowQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                TrendingNowQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    TrendingNowQuery.Medium(
                                        __typename = "Media",
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            TrendingNowQuery.NextAiringEpisode(
                                                __typename = "AiringSchedule",
                                                episode = 12,
                                            ),
                                        isAdult = false,
                                        type = NetworkMediaType.ANIME,
                                        description = "",
                                        genres = emptyList(),
                                        meanScore = 90,
                                        isFavourite = false,
                                        format = NetworkMediaFormat.TV,
                                        bannerImage = "",
                                        countryOfOrigin = "JP",
                                        coverImage =
                                            TrendingNowQuery.CoverImage(
                                                __typename = "MediaCoverImage",
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            TrendingNowQuery.Title(
                                                __typename = "MediaTitle",
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            TrendingNowQuery.MediaListEntry(
                                                __typename = "MediaList",
                                                progress = 12,
                                                private = false,
                                                score = 10.0,
                                                status = null,
                                            ),
                                        rankings = null,
                                    ),
                                ),
                        ),
                )

            // Create the query with test parameters
            val query =
                TrendingNowQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    assertEquals("Test Anime", title.romaji)
                    assertEquals("Test Anime EN", title.english)
                    assertEquals(123, idAniList)
                    assertEquals(12, episodes)
                    assertEquals(90, meanScore)
                    assertEquals(MediaFormat.TV, format)
                }
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getTrendingNowMediaList returns empty list when API returns null media list`() =
        runTest {
            // Given
            val testData =
                TrendingNowQuery.Data(
                    Page =
                        TrendingNowQuery.Page(
                            __typename = "Page",
                            pageInfo = null,
                            media = null,
                        ),
                )

            val query =
                TrendingNowQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                assertNull(page.pageInfo)
            }
        }

    @Test
    fun `getTrendingNowMediaList returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                TrendingNowQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getTrendingNowMediaList returns failure result when ApolloException occurs`() =
        runTest {
            // Given
            TrendingNowQuery(
                page = defaultParams.pageNumber,
                perPage = Optional.present(defaultParams.perPage),
                mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
            )

            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network error", exception.message)
            }
        }

    @Test
    fun `getPopularMediaList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                SeasonalAnimeQuery.Data(
                    Page =
                        SeasonalAnimeQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                SeasonalAnimeQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    SeasonalAnimeQuery.Medium(
                                        __typename = "Media",
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            SeasonalAnimeQuery.NextAiringEpisode(
                                                __typename = "AiringSchedule",
                                                episode = 12,
                                            ),
                                        isAdult = false,
                                        type = NetworkMediaType.ANIME,
                                        description = "",
                                        genres = emptyList(),
                                        meanScore = 90,
                                        isFavourite = false,
                                        format = NetworkMediaFormat.TV,
                                        bannerImage = "",
                                        countryOfOrigin = "JP",
                                        coverImage =
                                            SeasonalAnimeQuery.CoverImage(
                                                __typename = "MediaCoverImage",
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            SeasonalAnimeQuery.Title(
                                                __typename = "MediaTitle",
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            SeasonalAnimeQuery.MediaListEntry(
                                                __typename = "MediaList",
                                                progress = 12,
                                                private = false,
                                                score = 10.0,
                                                status = null,
                                            ),
                                    ),
                                ),
                        ),
                )

            // Create the query with test parameters
            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    assertEquals("Test Anime", title.romaji)
                    assertEquals("Test Anime EN", title.english)
                    assertEquals(123, idAniList)
                    assertEquals(12, episodes)
                    assertEquals(90, meanScore)
                    assertEquals(MediaFormat.TV, format)
                    assertEquals("JP", countryOfOrigin)
                }
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getPopularMediaList returns empty list when API returns null media list`() =
        runTest {
            // Given
            val testData =
                SeasonalAnimeQuery.Data(
                    Page =
                        SeasonalAnimeQuery.Page(
                            __typename = "Page",
                            pageInfo = null,
                            media = null,
                        ),
                )

            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                )

            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                assertNull(page.pageInfo)
            }
        }

    @Test
    fun `getPopularMediaList returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                SeasonalAnimeQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaType = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getPopularMediaList returns failure result when ApolloException occurs`() =
        runTest {
            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network error", exception.message)
            }
        }

    @Test
    fun `getMediaById returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                MediaQuery.Data(
                    Media =
                        MediaQuery.Media(
                            __typename = "Media",
                            id = 123,
                            idMal = 123,
                            status = NetworkMediaStatus.FINISHED,
                            chapters = 30,
                            episodes = 12,
                            nextAiringEpisode =
                                MediaQuery.NextAiringEpisode(
                                    __typename = "AiringSchedule",
                                    episode = 12,
                                    airingAt = 1698624000,
                                    timeUntilAiring = 0,
                                ),
                            isAdult = false,
                            type = NetworkMediaType.ANIME,
                            description = "",
                            genres = emptyList(),
                            meanScore = 90,
                            averageScore = 90,
                            characters =
                                MediaQuery.Characters(
                                    __typename = "CharacterConnection",
                                    edges =
                                        listOf(
                                            MediaQuery.Edge3(
                                                __typename = "CharacterEdge",
                                                role = null,
                                                node =
                                                    MediaQuery.Node3(
                                                        __typename = "Character",
                                                        id = 5,
                                                        name =
                                                            MediaQuery.Name(
                                                                __typename = "CharacterName",
                                                                full = "Saitama",
                                                            ),
                                                        image = null,
                                                    ),
                                            ),
                                        ),
                                ),
                            isFavourite = false,
                            format = NetworkMediaFormat.TV,
                            bannerImage = "",
                            countryOfOrigin = "JP",
                            coverImage =
                                MediaQuery.CoverImage(
                                    __typename = "MediaCoverImage",
                                    large = "",
                                    extraLarge = "",
                                ),
                            title =
                                MediaQuery.Title(
                                    __typename = "MediaTitle",
                                    romaji = "Test Anime",
                                    english = "Test Anime EN",
                                    native = "Test Anime",
                                    userPreferred = "Test Anime EN",
                                ),
                            mediaListEntry =
                                MediaQuery.MediaListEntry(
                                    __typename = "MediaList",
                                    id = 123,
                                    startedAt = null,
                                    completedAt = null,
                                    progress = 12,
                                    private = false,
                                    score = 10.0,
                                    status = null,
                                    notes = "",
                                    repeat = 0,
                                ),
                            duration = 12,
                            startDate =
                                MediaQuery.StartDate(
                                    __typename = "FuzzyDate",
                                    year = 2000,
                                    month = 12,
                                    day = 12,
                                ),
                            endDate =
                                MediaQuery.EndDate(
                                    __typename = "FuzzyDate",
                                    year = 2010,
                                    month = 11,
                                    day = 21,
                                ),
                            externalLinks = null,
                            favourites = 4590,
                            popularity = 1000,
                            season = NetworkMediaSeason.FALL,
                            seasonYear = 2000,
                            rankings = null,
                            recommendations = null,
                            relations = null,
                            reviews = null,
                            siteUrl = "siteurl",
                            source = null,
                            staff = null,
                            synonyms = listOf("action", "adventure"),
                            stats = null,
                            studios = null,
                            tags = null,
                            trailer = null,
                            trending = 10,
                        ),
                )

            // Create the query with test parameters
            val query =
                MediaQuery(
                    id = defaultParams.mediaId,
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
                    fetchFromNetwork = false,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { media ->
                assertEquals(123, media.idAniList)
                assertEquals("Test Anime", media.title.romaji)
                assertEquals(90, media.meanScore)
                assertEquals(10, media.trending)
            }
        }

    @Test
    fun `getMediaById returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                MediaQuery(
                    id = defaultParams.mediaId,
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
                    fetchFromNetwork = false,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getMediaById returns failure result when ApolloException occurs`() =
        runTest {
            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Unknown GraphQL error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
                    fetchFromNetwork = false,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Unknown GraphQL error", exception.message)
            }
        }

    @Test
    fun `getMediaThreads returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                MediaThreadsQuery.Data(
                    Page =
                        MediaThreadsQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                MediaThreadsQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            threads =
                                listOf(
                                    MediaThreadsQuery.Thread(
                                        __typename = "Thread",
                                        id = 50,
                                        title = "Media Thread",
                                        body = "Media Thread Body",
                                        isLiked = true,
                                        isLocked = false,
                                        isSubscribed = false,
                                        likeCount = 30,
                                        totalReplies = 25,
                                        viewCount = 450,
                                        createdAt = 123456,
                                        user =
                                            MediaThreadsQuery.User(
                                                __typename = "User",
                                                id = 79,
                                                name = "User",
                                                avatar = null,
                                            ),
                                    ),
                                ),
                        ),
                )

            // Create the query with test parameters
            val query =
                MediaThreadsQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaCategoryId = Optional.present(defaultParams.mediaId),
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    assertEquals("Media Thread", title)
                    assertEquals("Media Thread Body", body)
                    assertEquals(50, id)
                    assertEquals(30, likeCount)
                    assertEquals(25, replyCount)
                    assertEquals("User", user?.name)
                }
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getMediaThreads returns empty list when API returns null thread list`() =
        runTest {
            // Given
            val testData =
                MediaThreadsQuery.Data(
                    Page =
                        MediaThreadsQuery.Page(
                            __typename = "Page",
                            pageInfo = null,
                            threads = null,
                        ),
                )

            val query =
                MediaThreadsQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaCategoryId = Optional.present(defaultParams.mediaId),
                )

            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                assertNull(page.pageInfo)
            }
        }

    @Test
    fun `getMediaThreads returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                MediaThreadsQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    mediaCategoryId = Optional.present(defaultParams.mediaId),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getMediaThreads returns failure result when ApolloException occurs`() =
        runTest {
            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network error", exception.message)
            }
        }

    @Test
    fun `getSearchMedia returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                MediaSearchQuery.Data(
                    Page =
                        MediaSearchQuery.Page(
                            __typename = "Page",
                            pageInfo =
                                MediaSearchQuery.PageInfo(
                                    __typename = "PageInfo",
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    MediaSearchQuery.Medium(
                                        __typename = "Media",
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            MediaSearchQuery.NextAiringEpisode(
                                                __typename = "AiringSchedule",
                                                episode = 12,
                                            ),
                                        isAdult = false,
                                        type = NetworkMediaType.ANIME,
                                        description = "",
                                        genres = emptyList(),
                                        meanScore = 90,
                                        isFavourite = false,
                                        format = NetworkMediaFormat.TV,
                                        bannerImage = "",
                                        countryOfOrigin = "JP",
                                        coverImage =
                                            MediaSearchQuery.CoverImage(
                                                __typename = "MediaCoverImage",
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            MediaSearchQuery.Title(
                                                __typename = "MediaTitle",
                                                romaji = "One Piece",
                                                english = "One Piece",
                                                userPreferred = "One Piece",
                                            ),
                                        mediaListEntry =
                                            MediaSearchQuery.MediaListEntry(
                                                __typename = "MediaList",
                                                progress = 12,
                                                private = false,
                                                score = 10.0,
                                                status = null,
                                            ),
                                    ),
                                ),
                        ),
                )

            // Create the query with test parameters
            val query =
                MediaSearchQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.presentIfNotNull(defaultParams.seasonYear),
                    season = Optional.presentIfNotNull(defaultParams.season.toNetworkMediaSeason()),
                    type = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    search = Optional.presentIfNotNull(defaultParams.searchQuery),
                    format = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    status = Optional.presentIfNotNull(defaultParams.mediaStatus?.toNetworkMediaStatus()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                    genres = Optional.presentIfNotNull(defaultParams.genres),
                    tags = Optional.presentIfNotNull(defaultParams.tags),
                    sort = Optional.presentIfNotNull(defaultParams.sort?.map { it.toNetworkMediaSort() }),
                )

            // Enqueue the test response
            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                with(page.data.first()) {
                    assertEquals("One Piece", title.romaji)
                    assertEquals("One Piece", title.english)
                    assertEquals(123, idAniList)
                    assertEquals(12, episodes)
                    assertEquals(90, meanScore)
                    assertEquals(MediaFormat.TV, format)
                }
                with(page.pageInfo!!) {
                    assertEquals(1, currentPage)
                    assertEquals(5, total)
                    assertTrue(hasNextPage == true)
                }
            }
        }

    @Test
    fun `getSearchMedia returns empty list when API returns null media list`() =
        runTest {
            // Given
            val testData =
                MediaSearchQuery.Data(
                    Page =
                        MediaSearchQuery.Page(
                            __typename = "Page",
                            pageInfo = null,
                            media = null,
                        ),
                )

            val query =
                MediaSearchQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.presentIfNotNull(defaultParams.seasonYear),
                    season = Optional.presentIfNotNull(defaultParams.season.toNetworkMediaSeason()),
                    type = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    search = Optional.presentIfNotNull(defaultParams.searchQuery),
                    format = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    status = Optional.presentIfNotNull(defaultParams.mediaStatus?.toNetworkMediaStatus()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                    genres = Optional.presentIfNotNull(defaultParams.genres),
                    tags = Optional.presentIfNotNull(defaultParams.tags),
                    sort = Optional.presentIfNotNull(defaultParams.sort?.map { it.toNetworkMediaSort() }),
                )

            testClient.enqueueTestResponse(operation = query, data = testData, errors = null)

            // When
            val result =
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertTrue(page.data.isEmpty())
                assertNull(page.pageInfo)
            }
        }

    @Test
    fun `getSearchMedia returns failure result when API returns errors`() =
        runTest {
            // Given
            val query =
                MediaSearchQuery(
                    page = defaultParams.pageNumber,
                    perPage = Optional.present(defaultParams.perPage),
                    seasonYear = Optional.presentIfNotNull(defaultParams.seasonYear),
                    season = Optional.presentIfNotNull(defaultParams.season.toNetworkMediaSeason()),
                    type = Optional.present(defaultParams.mediaType.toNetworkMediaType()),
                    search = Optional.presentIfNotNull(defaultParams.searchQuery),
                    format = Optional.presentIfNotNull(defaultParams.mediaFormat?.toNetworkMediaFormat()),
                    status = Optional.presentIfNotNull(defaultParams.mediaStatus?.toNetworkMediaStatus()),
                    countryOfOrigin = Optional.presentIfNotNull(defaultParams.countryOfOrigin),
                    genres = Optional.presentIfNotNull(defaultParams.genres),
                    tags = Optional.presentIfNotNull(defaultParams.tags),
                    sort = Optional.presentIfNotNull(defaultParams.sort?.map { it.toNetworkMediaSort() }),
                )

            testClient.enqueueTestResponse(
                operation = query,
                data = null,
                errors =
                    listOf(
                        Error.Builder(message = "GraphQL Error").build(),
                    ),
            )

            // When
            val result =
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertEquals("GraphQL Error", exception.message)
            }
        }

    @Test
    fun `getSearchMedia returns failure result when ApolloException occurs`() =
        runTest {
            // Create custom transport that throws exception
            val errorClient =
                ApolloClient
                    .Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> = throw ApolloNetworkException("Network error")

                            override fun dispose() {}
                        },
                    ).build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isFailure)
            result.onFailure { exception ->
                assertTrue(exception is ApolloException)
                assertEquals("Network error", exception.message)
            }
        }

    @Test
    fun `getUserDetails returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                ViewerQuery.Data(
                    Viewer =
                        ViewerQuery.Viewer(
                            __typename = "User",
                            id = 1,
                            name = "Test User",
                            bannerImage = "banner.jpg",
                            unreadNotificationCount = 5,
                            avatar =
                                ViewerQuery.Avatar(
                                    __typename = "UserAvatar",
                                    medium = "avatar.jpg",
                                ),
                            options =
                                ViewerQuery.Options(
                                    __typename = "UserOptions",
                                    displayAdultContent = true,
                                ),
                            mediaListOptions =
                                ViewerQuery.MediaListOptions(
                                    __typename = "MediaListOptions",
                                    rowOrder = "title",
                                    animeList =
                                        ViewerQuery.AnimeList(
                                            __typename = "MediaListTypeOptions",
                                            sectionOrder = listOf("Watching", "Completed"),
                                            customLists = listOf("Plan to Watch"),
                                        ),
                                    mangaList =
                                        ViewerQuery.MangaList(
                                            __typename = "MediaListTypeOptions",
                                            sectionOrder = listOf("Reading", "Completed"),
                                            customLists = listOf("Plan to Read"),
                                        ),
                                ),
                            statistics =
                                ViewerQuery.Statistics(
                                    __typename = "UserStatistics",
                                    anime =
                                        ViewerQuery.Anime(
                                            __typename = "UserStatisticsResource",
                                            episodesWatched = 100,
                                            count = 50,
                                        ),
                                    manga =
                                        ViewerQuery.Manga(
                                            __typename = "UserStatisticsResource",
                                            chaptersRead = 200,
                                            count = 30,
                                        ),
                                ),
                        ),
                )

            testClient.enqueueTestResponse(operation = ViewerQuery(), data = testData)

            // When
            val result = mediaService.getUserDetails()

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { user ->
                assertEquals(1, user.id)
                assertEquals("Test User", user.name)
                assertEquals(5, user.unreadNotificationCount)
                assertTrue(user.options.displayAdultContent)
            }
        }

    @Test
    fun `getUserListCollection returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                UserListCollectionQuery.Data(
                    MediaListCollection =
                        UserListCollectionQuery.MediaListCollection(
                            __typename = "MediaListCollection",
                            lists =
                                listOf(
                                    UserListCollectionQuery.List(
                                        __typename = "MediaListGroup",
                                        name = "Watching",
                                        isCustomList = false,
                                        entries =
                                            listOf(
                                                UserListCollectionQuery.Entry(
                                                    __typename = "MediaList",
                                                    id = 1,
                                                    status = NetworkMediaListStatus.CURRENT,
                                                    score = 8.5,
                                                    advancedScores = null,
                                                    progress = 5,
                                                    progressVolumes = null,
                                                    repeat = 0,
                                                    private = false,
                                                    hiddenFromStatusLists = false,
                                                    notes = "Good anime",
                                                    mediaId = 123,
                                                    media =
                                                        UserListCollectionQuery.Media(
                                                            __typename = "Media",
                                                            id = 123,
                                                            idMal = 123,
                                                            status = NetworkMediaStatus.RELEASING,
                                                            chapters = null,
                                                            episodes = 12,
                                                            duration = 24,
                                                            startDate = null,
                                                            endDate = null,
                                                            season = NetworkMediaSeason.SPRING,
                                                            seasonYear = 2024,
                                                            nextAiringEpisode = null,
                                                            isAdult = false,
                                                            type = NetworkMediaType.ANIME,
                                                            genres = listOf("Action"),
                                                            meanScore = 80,
                                                            averageScore = 80,
                                                            description = "Description",
                                                            synonyms = emptyList<String>(),
                                                            source = null,
                                                            isFavourite = false,
                                                            format = NetworkMediaFormat.TV,
                                                            bannerImage = "banner.jpg",
                                                            countryOfOrigin = "JP",
                                                            coverImage =
                                                                UserListCollectionQuery.CoverImage(
                                                                    __typename = "MediaCoverImage",
                                                                    large = "large.jpg",
                                                                    extraLarge = "xl.jpg",
                                                                ),
                                                            title =
                                                                UserListCollectionQuery.Title(
                                                                    __typename = "MediaTitle",
                                                                    english = "Test Anime EN",
                                                                    romaji = "Test Anime",
                                                                    native = "Test Anime",
                                                                    userPreferred = "Test Anime",
                                                                ),
                                                            mediaListEntry = null,
                                                            trailer = null,
                                                            externalLinks = null,
                                                            popularity = 100,
                                                            trending = 10,
                                                            favourites = 50,
                                                            rankings = null,
                                                            siteUrl = "url",
                                                            stats = null,
                                                        ),
                                                ),
                                            ),
                                    ),
                                ),
                            hasNextChunk = false,
                        ),
                )

            val query =
                UserListCollectionQuery(
                    userId = Optional.present(1),
                    type = Optional.present(NetworkMediaType.ANIME),
                    sort = Optional.present(listOf(NetworkMediaListSort.UPDATED_TIME_DESC)),
                    chunk = Optional.present(1),
                    perChunk = Optional.present(20),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getUserListCollection(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = MediaType.ANIME,
                    userId = 1,
                    sortBy = null,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                val media = page.data.first()
                assertEquals(123, media.idAniList)
                assertNotNull(media.mediaListEntry)
                assertEquals(1, media.mediaListEntry?.id)
                assertEquals(8.5, media.mediaListEntry?.score ?: 0.0, 0.0)
            }
        }

    @Test
    fun `getMediaRecommendationsById returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                MediaRecommendationsQuery.Data(
                    Media =
                        MediaRecommendationsQuery.Media(
                            __typename = "Media",
                            id = 123,
                            idMal = 123,
                            recommendations =
                                MediaRecommendationsQuery.Recommendations(
                                    __typename = "RecommendationConnection",
                                    edges =
                                        listOf(
                                            MediaRecommendationsQuery.Edge(
                                                __typename = "RecommendationEdge",
                                                node =
                                                    MediaRecommendationsQuery.Node(
                                                        __typename = "Recommendation",
                                                        mediaRecommendation =
                                                            MediaRecommendationsQuery.MediaRecommendation(
                                                                __typename = "Media",
                                                                id = 456,
                                                                idMal = 789,
                                                                title =
                                                                    MediaRecommendationsQuery.Title(
                                                                        __typename = "MediaTitle",
                                                                        english = "Recommended Anime EN",
                                                                        romaji = "Recommended Anime",
                                                                    ),
                                                                type = NetworkMediaType.ANIME,
                                                                format = NetworkMediaFormat.TV,
                                                                status = NetworkMediaStatus.FINISHED,
                                                                genres = listOf("Action"),
                                                                episodes = 12,
                                                                chapters = null,
                                                                bannerImage = "banner.jpg",
                                                                volumes = null,
                                                                coverImage =
                                                                    MediaRecommendationsQuery.CoverImage(
                                                                        __typename = "MediaCoverImage",
                                                                        large = "large.jpg",
                                                                        extraLarge = "xl.jpg",
                                                                    ),
                                                                meanScore = 85,
                                                                nextAiringEpisode = null,
                                                            ),
                                                    ),
                                            ),
                                        ),
                                ),
                        ),
                )

            val query = MediaRecommendationsQuery(id = 123)
            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result = mediaService.getMediaRecommendationsById(id = 123)

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                val media = page.data.first()
                assertEquals(456, media.idAniList)
                assertEquals("Recommended Anime", media.title.romaji)
            }
        }

    @Test
    fun `saveMediaListEntry returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                SaveMediaListEntryMutation.Data(
                    SaveMediaListEntry =
                        SaveMediaListEntryMutation.SaveMediaListEntry(
                            __typename = "MediaList",
                            id = 1,
                            status = NetworkMediaListStatus.CURRENT,
                            score = 8.5,
                            progress = 5,
                            repeat = 0,
                            private = false,
                            notes = "Good anime",
                            hiddenFromStatusLists = false,
                            startedAt =
                                SaveMediaListEntryMutation.StartedAt(
                                    __typename = "FuzzyDate",
                                    year = 2024,
                                    month = 1,
                                    day = 1,
                                ),
                            completedAt = null,
                        ),
                )

            val mutation =
                SaveMediaListEntryMutation(
                    mediaId = 123,
                    status = Optional.present(NetworkMediaListStatus.CURRENT),
                )

            testClient.enqueueTestResponse(operation = mutation, data = testData)

            // When
            val result =
                mediaService.saveMediaListEntry(
                    mediaId = 123,
                    status = MediaListStatus.CURRENT,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { entry ->
                assertEquals(1, entry.id)
                assertEquals(MediaListStatus.CURRENT, entry.status)
                assertEquals(8.5, entry.score, 0.0)
            }
        }

    @Test
    fun `deleteMediaListEntry returns success result when API call is successful`() =
        runTest {
            // Given
            val testData =
                DeleteMediaListEntryMutation.Data(
                    DeleteMediaListEntry =
                        DeleteMediaListEntryMutation.DeleteMediaListEntry(
                            __typename = "Deleted",
                            deleted = true,
                        ),
                )

            val mutation = DeleteMediaListEntryMutation(mediaListEntryId = Optional.present(1))
            testClient.enqueueTestResponse(operation = mutation, data = testData)

            // When
            val result = mediaService.deleteMediaListEntry(mediaListEntryId = 1)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(true, result.getOrNull())
        }

    @Test
    fun `toggleFavourite returns success result when API call is successful`() =
        runTest {
            // Given
            val testData =
                ToggleFavouriteMutation.Data(
                    ToggleFavourite =
                        ToggleFavouriteMutation.ToggleFavourite(
                            __typename = "ToggleFavourite",
                            anime =
                                ToggleFavouriteMutation.Anime(
                                    __typename = "MediaConnection",
                                    pageInfo =
                                        ToggleFavouriteMutation.PageInfo(
                                            __typename = "PageInfo",
                                            currentPage = 1,
                                        ),
                                ),
                        ),
                )

            val mutation =
                ToggleFavouriteMutation(
                    animeId = Optional.present(123),
                    mangaId = Optional.absent(),
                )
            testClient.enqueueTestResponse(operation = mutation, data = testData)

            // When
            val result = mediaService.toggleFavourite(animeId = 123)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(true, result.getOrNull())
        }

    @Test
    fun `getNotifications returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                NotificationsQuery.Data(
                    Page =
                        NotificationsQuery.Page(
                            __typename = "Page",
                            notifications =
                                listOf(
                                    NotificationsQuery.Notification(
                                        __typename = "AiringNotification",
                                        onAiringNotification =
                                            NotificationsQuery.OnAiringNotification(
                                                id = 1,
                                                contexts = listOf("Episode", " aired"),
                                                animeId = 123,
                                                episode = 5,
                                                media =
                                                    NotificationsQuery.Media(
                                                        __typename = "Media",
                                                        title =
                                                            NotificationsQuery.Title(
                                                                __typename = "MediaTitle",
                                                                userPreferred = "Test Anime",
                                                            ),
                                                        coverImage =
                                                            NotificationsQuery.CoverImage(
                                                                __typename = "MediaCoverImage",
                                                                medium = "medium.jpg",
                                                                large = "large.jpg",
                                                            ),
                                                        type = NetworkMediaType.ANIME,
                                                    ),
                                                type = NetworkNotificationType.AIRING,
                                                createdAt = 123456789,
                                            ),
                                        onRelatedMediaAdditionNotification = null,
                                        onMediaDataChangeNotification = null,
                                        onMediaMergeNotification = null,
                                        onMediaDeletionNotification = null,
                                    ),
                                ),
                            pageInfo =
                                NotificationsQuery.PageInfo(
                                    __typename = "PageInfo",
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                        ),
                )

            val query =
                NotificationsQuery(
                    page = Optional.present(1),
                    perPage = Optional.present(20),
                    resetCount = Optional.present(true),
                    typeIn = Optional.absent(),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getNotifications(
                    pageNumber = 1,
                    perPage = 20,
                    resetCount = true,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
                assertEquals(1, page.pageInfo?.currentPage)
            }
        }

    @Test
    fun `clearCache completes successfully`() =
        runTest {
            mediaService.clearCache()
        }

    @Test
    fun `getAnimeByStatusList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                UserListCollectionQuery.Data(
                    MediaListCollection =
                        UserListCollectionQuery.MediaListCollection(
                            __typename = "MediaListCollection",
                            lists =
                                listOf(
                                    UserListCollectionQuery.List(
                                        __typename = "MediaListGroup",
                                        name = "Watching",
                                        isCustomList = false,
                                        entries =
                                            listOf(
                                                UserListCollectionQuery.Entry(
                                                    __typename = "MediaList",
                                                    id = 1,
                                                    status = NetworkMediaListStatus.CURRENT,
                                                    score = 8.5,
                                                    advancedScores = null,
                                                    progress = 5,
                                                    progressVolumes = null,
                                                    repeat = 0,
                                                    private = false,
                                                    hiddenFromStatusLists = false,
                                                    notes = "Good anime",
                                                    mediaId = 123,
                                                    media =
                                                        UserListCollectionQuery.Media(
                                                            __typename = "Media",
                                                            id = 123,
                                                            idMal = 123,
                                                            status = NetworkMediaStatus.RELEASING,
                                                            chapters = null,
                                                            episodes = 12,
                                                            duration = 24,
                                                            startDate = null,
                                                            endDate = null,
                                                            season = NetworkMediaSeason.SPRING,
                                                            seasonYear = 2024,
                                                            nextAiringEpisode = null,
                                                            isAdult = false,
                                                            type = NetworkMediaType.ANIME,
                                                            genres = listOf("Action"),
                                                            meanScore = 80,
                                                            averageScore = 80,
                                                            description = "Description",
                                                            synonyms = emptyList<String>(),
                                                            source = null,
                                                            isFavourite = false,
                                                            format = NetworkMediaFormat.TV,
                                                            bannerImage = "banner.jpg",
                                                            countryOfOrigin = "JP",
                                                            coverImage =
                                                                UserListCollectionQuery.CoverImage(
                                                                    __typename = "MediaCoverImage",
                                                                    large = "large.jpg",
                                                                    extraLarge = "xl.jpg",
                                                                ),
                                                            title =
                                                                UserListCollectionQuery.Title(
                                                                    __typename = "MediaTitle",
                                                                    english = "Test Anime EN",
                                                                    romaji = "Test Anime",
                                                                    native = "Test Anime",
                                                                    userPreferred = "Test Anime",
                                                                ),
                                                            mediaListEntry = null,
                                                            trailer = null,
                                                            externalLinks = null,
                                                            popularity = 100,
                                                            trending = 10,
                                                            favourites = 50,
                                                            rankings = null,
                                                            siteUrl = "url",
                                                            stats = null,
                                                        ),
                                                ),
                                            ),
                                    ),
                                ),
                            hasNextChunk = false,
                        ),
                )

            val query =
                UserListCollectionQuery(
                    userId = Optional.absent(),
                    type = Optional.present(NetworkMediaType.ANIME),
                    status = Optional.present(NetworkMediaListStatus.CURRENT),
                    sort = Optional.present(listOf(NetworkMediaListSort.UPDATED_TIME_DESC)),
                    chunk = Optional.present(1),
                    perChunk = Optional.present(20),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getAnimeByStatusList(
                    pageNumber = 1,
                    perPage = 20,
                    status = MediaListStatus.CURRENT,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
            }
        }

    @Test
    fun `getMangaByStatusList returns success result with mapped data when API call is successful`() =
        runTest {
            // Given
            val testData =
                UserListCollectionQuery.Data(
                    MediaListCollection =
                        UserListCollectionQuery.MediaListCollection(
                            __typename = "MediaListCollection",
                            lists =
                                listOf(
                                    UserListCollectionQuery.List(
                                        __typename = "MediaListGroup",
                                        name = "Reading",
                                        isCustomList = false,
                                        entries =
                                            listOf(
                                                UserListCollectionQuery.Entry(
                                                    __typename = "MediaList",
                                                    id = 1,
                                                    status = NetworkMediaListStatus.CURRENT,
                                                    score = 8.5,
                                                    advancedScores = null,
                                                    progress = 5,
                                                    progressVolumes = null,
                                                    repeat = 0,
                                                    private = false,
                                                    hiddenFromStatusLists = false,
                                                    notes = "Good manga",
                                                    mediaId = 123,
                                                    media =
                                                        UserListCollectionQuery.Media(
                                                            __typename = "Media",
                                                            id = 123,
                                                            idMal = 123,
                                                            status = NetworkMediaStatus.FINISHED,
                                                            chapters = 30,
                                                            episodes = null,
                                                            duration = null,
                                                            startDate = null,
                                                            endDate = null,
                                                            season = null,
                                                            seasonYear = null,
                                                            nextAiringEpisode = null,
                                                            isAdult = false,
                                                            type = NetworkMediaType.MANGA,
                                                            genres = listOf("Adventure"),
                                                            meanScore = 80,
                                                            averageScore = 80,
                                                            description = "Description",
                                                            synonyms = emptyList<String>(),
                                                            source = null,
                                                            isFavourite = false,
                                                            format = NetworkMediaFormat.MANGA,
                                                            bannerImage = "banner.jpg",
                                                            countryOfOrigin = "JP",
                                                            coverImage =
                                                                UserListCollectionQuery.CoverImage(
                                                                    __typename = "MediaCoverImage",
                                                                    large = "large.jpg",
                                                                    extraLarge = "xl.jpg",
                                                                ),
                                                            title =
                                                                UserListCollectionQuery.Title(
                                                                    __typename = "MediaTitle",
                                                                    english = "Test Manga EN",
                                                                    romaji = "Test Manga",
                                                                    native = "Test Manga",
                                                                    userPreferred = "Test Manga",
                                                                ),
                                                            mediaListEntry = null,
                                                            trailer = null,
                                                            externalLinks = null,
                                                            popularity = 100,
                                                            trending = 10,
                                                            favourites = 50,
                                                            rankings = null,
                                                            siteUrl = "url",
                                                            stats = null,
                                                        ),
                                                ),
                                            ),
                                    ),
                                ),
                            hasNextChunk = false,
                        ),
                )

            val query =
                UserListCollectionQuery(
                    userId = Optional.absent(),
                    type = Optional.present(NetworkMediaType.MANGA),
                    status = Optional.present(NetworkMediaListStatus.CURRENT),
                    sort = Optional.present(listOf(NetworkMediaListSort.UPDATED_TIME_DESC)),
                    chunk = Optional.present(1),
                    perChunk = Optional.present(20),
                )

            testClient.enqueueTestResponse(operation = query, data = testData)

            // When
            val result =
                mediaService.getMangaByStatusList(
                    pageNumber = 1,
                    perPage = 20,
                    status = MediaListStatus.CURRENT,
                )

            // Then
            assertTrue(result.isSuccess)
            result.onSuccess { page ->
                assertEquals(1, page.data.size)
            }
        }

    private data class TestParams(
        val pageNumber: Int,
        val perPage: Int,
        val seasonYear: Int,
        val season: MediaSeason,
        val mediaType: MediaType,
        val mediaFormat: MediaFormat?,
        val mediaStatus: MediaStatus?,
        val airingAtLesser: Int,
        val airingAtGreater: Int,
        val countryOfOrigin: String?,
        val mediaId: Int,
        val searchQuery: String?,
        val genres: List<String>?,
        val tags: List<String>?,
        val sort: List<MediaSort>?,
    )
}
