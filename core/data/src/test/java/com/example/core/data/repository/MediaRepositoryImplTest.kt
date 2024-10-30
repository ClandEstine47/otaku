package com.example.core.data.repository

import com.apollographql.apollo3.exception.ApolloException
import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaTitle
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.service.MediaService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MediaRepositoryImplTest {
    private lateinit var mediaService: MediaService
    private lateinit var mediaRepository: MediaRepositoryImpl

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
        mediaService = mockk()
        mediaRepository = MediaRepositoryImpl(mediaService)
    }

    @Test
    fun `getSeasonalMedia returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            Media(idAniList = 1, title = MediaTitle(english = "One Piece")),
                            Media(idAniList = 2, title = MediaTitle(english = "Naruto")),
                        ),
                )

            coEvery {
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getSeasonalMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getSeasonalMedia with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<Media>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getSeasonalMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getSeasonalMedia returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getSeasonalMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getSeasonalMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            AiringSchedule(episode = 1120, media = Media(idAniList = 1, title = MediaTitle(english = "One Piece"))),
                            AiringSchedule(episode = null, media = Media(idAniList = 2, title = MediaTitle(english = "Naruto"))),
                        ),
                )

            coEvery {
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getRecentlyUpdatedAnimeList with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<AiringSchedule>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getRecentlyUpdatedAnimeList returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    airingAtLesser = defaultParams.airingAtLesser,
                    airingAtGreater = defaultParams.airingAtGreater,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getTrendingNowMedia returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            Media(idAniList = 1, title = MediaTitle(english = "One Piece")),
                            Media(idAniList = 2, title = MediaTitle(english = "Naruto")),
                        ),
                )

            coEvery {
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getTrendingNowMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getTrendingNowMedia with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<Media>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getTrendingNowMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getTrendingNowMedia returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getTrendingNowMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getTrendingNowMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getPopularMedia returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            Media(idAniList = 1, title = MediaTitle(english = "One Piece")),
                            Media(idAniList = 2, title = MediaTitle(english = "Naruto")),
                        ),
                )

            coEvery {
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getPopularMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getPopularMedia with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<Media>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getPopularMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getPopularMedia returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getPopularMediaList(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getPopularMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    mediaFormat = defaultParams.mediaFormat,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getMediaById returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedMedia = Media(idAniList = 1, title = MediaTitle(english = "One Piece"))

            coEvery {
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
                )
            } returns Result.success(expectedMedia)

            // When
            val result =
                mediaRepository.getMediaById(
                    id = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedMedia, result.getOrNull())
        }

    @Test
    fun `getMediaById returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getMediaById(
                    id = defaultParams.mediaId,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getMediaById(
                    id = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getMediaThreads returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            Thread(id = 1, title = "Title1", body = "Body1"),
                            Thread(id = 2, title = "Title2", body = "Body2"),
                        ),
                )

            coEvery {
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getMediaThreads with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<Thread>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getMediaThreads returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getMediaThreads(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaId = defaultParams.mediaId,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
        }

    @Test
    fun `getSearchMedia returns success when service call succeeds`() =
        runTest {
            // Given
            val expectedPage =
                Page(
                    pageInfo =
                        PageInfo(
                            total = 500,
                            perPage = 21,
                            currentPage = 1,
                            lastPage = null,
                            hasNextPage = true,
                        ),
                    data =
                        listOf(
                            Media(idAniList = 1, title = MediaTitle(english = "One Piece")),
                            Media(idAniList = 2, title = MediaTitle(english = "Naruto")),
                        ),
                )

            coEvery {
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    season = defaultParams.season,
                    seasonYear = defaultParams.seasonYear,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    search = defaultParams.searchQuery,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
        }

    @Test
    fun `getSearchMedia with empty page returns success with empty list`() =
        runTest {
            // Given
            val expectedPage =
                Page<Media>(
                    pageInfo =
                        PageInfo(
                            total = 0,
                            perPage = 20,
                            currentPage = 1,
                            hasNextPage = false,
                            lastPage = null,
                        ),
                    data = emptyList(),
                )

            coEvery {
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    season = defaultParams.season,
                    seasonYear = defaultParams.seasonYear,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )
            } returns Result.success(expectedPage)

            // When
            val result =
                mediaRepository.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    search = defaultParams.searchQuery,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedPage, result.getOrNull())
            assertEquals(0, result.getOrNull()?.data?.size)
        }

    @Test
    fun `getSearchMedia returns failure when Apollo throws exception`() =
        runTest {
            // Given
            val expectedError = ApolloException("Network error")

            coEvery {
                mediaService.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    mediaType = defaultParams.mediaType,
                    search = defaultParams.searchQuery,
                    season = defaultParams.season,
                    seasonYear = defaultParams.seasonYear,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )
            } returns Result.failure(expectedError)

            // When
            val result =
                mediaRepository.getSearchMedia(
                    pageNumber = defaultParams.pageNumber,
                    perPage = defaultParams.perPage,
                    search = defaultParams.searchQuery,
                    seasonYear = defaultParams.seasonYear,
                    season = defaultParams.season,
                    mediaType = defaultParams.mediaType,
                    format = defaultParams.mediaFormat,
                    status = defaultParams.mediaStatus,
                    countryOfOrigin = defaultParams.countryOfOrigin,
                    genres = defaultParams.genres,
                    tags = defaultParams.tags,
                    sortBy = defaultParams.sort,
                )

            // Then
            assertTrue(result.isFailure)
            assertEquals(expectedError, result.exceptionOrNull())
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
