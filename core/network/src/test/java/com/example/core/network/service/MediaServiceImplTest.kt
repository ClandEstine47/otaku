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
import com.example.core.domain.model.media.MediaType
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
            airingAtLesser = 1698710400,
            airingAtGreater = 1698624000,
            countryOfOrigin = "JP",
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
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat.toNetworkMediaFormat()),
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
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat.toNetworkMediaFormat()),
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
                    mediaFormat = Optional.presentIfNotNull(defaultParams.mediaFormat.toNetworkMediaFormat()),
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

    private data class TestParams(
        val pageNumber: Int,
        val perPage: Int,
        val seasonYear: Int,
        val season: MediaSeason,
        val mediaType: MediaType,
        val mediaFormat: MediaFormat,
        val airingAtLesser: Int,
        val airingAtGreater: Int,
        val countryOfOrigin: String,
    )
}
