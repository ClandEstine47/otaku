package com.example.core.network.service

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.NetworkTransport
import com.apollographql.apollo3.testing.QueueTestNetworkTransport
import com.apollographql.apollo3.testing.enqueueTestResponse
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.network.MediaQuery
import com.example.core.network.MediaSearchQuery
import com.example.core.network.MediaThreadsQuery
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.TrendingNowQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.example.core.network.type.MediaFormat as NetworkMediaFormat
import com.example.core.network.type.MediaSeason as NetworkMediaSeason
import com.example.core.network.type.MediaStatus as NetworkMediaStatus
import com.example.core.network.type.MediaType as NetworkMediaType

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
            ApolloClient.Builder()
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
                            pageInfo =
                                SeasonalAnimeQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    SeasonalAnimeQuery.Medium(
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            SeasonalAnimeQuery.NextAiringEpisode(
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
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            SeasonalAnimeQuery.Title(
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            SeasonalAnimeQuery.MediaListEntry(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
                            pageInfo =
                                RecentlyUpdatedQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            airingSchedules =
                                listOf(
                                    RecentlyUpdatedQuery.AiringSchedule(
                                        episode = 5,
                                        airingAt = 1698624100,
                                        media =
                                            RecentlyUpdatedQuery.Media(
                                                id = 456,
                                                idMal = 789,
                                                title =
                                                    RecentlyUpdatedQuery.Title(
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
                            pageInfo =
                                RecentlyUpdatedQuery.PageInfo(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network connection failed")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
                            pageInfo =
                                TrendingNowQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    TrendingNowQuery.Medium(
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            TrendingNowQuery.NextAiringEpisode(
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
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            TrendingNowQuery.Title(
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            TrendingNowQuery.MediaListEntry(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
                            pageInfo =
                                SeasonalAnimeQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    SeasonalAnimeQuery.Medium(
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            SeasonalAnimeQuery.NextAiringEpisode(
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
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            SeasonalAnimeQuery.Title(
                                                romaji = "Test Anime",
                                                english = "Test Anime EN",
                                                userPreferred = "Test Anime EN",
                                            ),
                                        mediaListEntry =
                                            SeasonalAnimeQuery.MediaListEntry(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
                            id = 123,
                            idMal = 123,
                            status = NetworkMediaStatus.FINISHED,
                            chapters = 30,
                            episodes = 12,
                            nextAiringEpisode =
                                MediaQuery.NextAiringEpisode(
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
                                    edges =
                                        listOf(
                                            MediaQuery.Edge3(
                                                role = null,
                                                node =
                                                    MediaQuery.Node3(
                                                        id = 5,
                                                        name =
                                                            MediaQuery.Name(
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
                                    large = "",
                                    extraLarge = "",
                                ),
                            title =
                                MediaQuery.Title(
                                    romaji = "Test Anime",
                                    english = "Test Anime EN",
                                    native = "Test Anime",
                                    userPreferred = "Test Anime EN",
                                ),
                            mediaListEntry =
                                MediaQuery.MediaListEntry(
                                    progress = 12,
                                    private = false,
                                    score = 10.0,
                                    status = null,
                                ),
                            duration = 12,
                            startDate =
                                MediaQuery.StartDate(
                                    year = 2000,
                                    month = 12,
                                    day = 12,
                                ),
                            endDate =
                                MediaQuery.EndDate(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
                    ),
            )

            // When
            val result =
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Unknown GraphQL error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

            mediaService = MediaServiceImpl(errorClient)

            // When
            val result =
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
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
                            pageInfo =
                                MediaThreadsQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            threads =
                                listOf(
                                    MediaThreadsQuery.Thread(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
                            pageInfo =
                                MediaSearchQuery.PageInfo(
                                    total = 5,
                                    currentPage = 1,
                                    hasNextPage = true,
                                ),
                            media =
                                listOf(
                                    MediaSearchQuery.Medium(
                                        id = 123,
                                        idMal = 123,
                                        status = NetworkMediaStatus.FINISHED,
                                        chapters = 30,
                                        episodes = 12,
                                        nextAiringEpisode =
                                            MediaSearchQuery.NextAiringEpisode(
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
                                                large = "",
                                                extraLarge = "",
                                            ),
                                        title =
                                            MediaSearchQuery.Title(
                                                romaji = "One Piece",
                                                english = "One Piece",
                                                userPreferred = "One Piece",
                                            ),
                                        mediaListEntry =
                                            MediaSearchQuery.MediaListEntry(
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
                        Error(
                            message = "GraphQL Error",
                            locations = null,
                            path = null,
                            extensions = null,
                            nonStandardFields = null,
                        ),
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
                ApolloClient.Builder()
                    .networkTransport(
                        object : NetworkTransport {
                            override fun <D : Operation.Data> execute(request: ApolloRequest<D>): Flow<ApolloResponse<D>> {
                                throw ApolloException("Network error")
                            }

                            override fun dispose() {}
                        },
                    )
                    .build()

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
