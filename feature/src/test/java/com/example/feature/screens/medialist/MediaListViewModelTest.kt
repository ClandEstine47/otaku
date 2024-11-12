package com.example.feature.screens.medialist

import com.example.core.domain.model.CalendarTab
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.MediaListItem
import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import com.example.feature.Utils.currentAnimeSeason
import com.example.feature.Utils.nextAnimeSeason
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MediaListViewModelTest {
    private lateinit var viewModel: MediaListViewModel
    private lateinit var mediaRepository: MediaRepository
    private val testDispatcher = StandardTestDispatcher()
    private val currentTime = LocalDateTime.now()
    private val currentAnimeSeason = currentTime.currentAnimeSeason()
    private val nextAnimeSeason = currentTime.nextAnimeSeason()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mediaRepository = mockk()
        viewModel = MediaListViewModel(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadMediaList should update state with success response for RECENTLY_UPDATED`() =
        runTest {
            // Given
            val mockMedia = AiringSchedule()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.ScheduleType(media)
                }

            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = 1,
                    perPage = 30,
                    airingAtLesser = any(),
                    airingAtGreater = any(),
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.ANIME, MediaListContentType.RECENTLY_UPDATED)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for CURRENT_SEASON`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getSeasonalMedia(
                    pageNumber = 1,
                    perPage = 21,
                    seasonYear = currentAnimeSeason.year,
                    season = currentAnimeSeason.season,
                    mediaType = MediaType.ANIME,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.ANIME, MediaListContentType.CURRENT_SEASON)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for POPULAR_NOW`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.ANIME,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.ANIME, MediaListContentType.POPULAR_NOW)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for NEXT_SEASON`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getSeasonalMedia(
                    pageNumber = 1,
                    perPage = 21,
                    seasonYear = nextAnimeSeason.year,
                    season = nextAnimeSeason.season,
                    mediaType = MediaType.ANIME,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.ANIME, MediaListContentType.NEXT_SEASON)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for POPULAR_MANGA`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.MANGA,
                    countryOfOrigin = "JP",
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.MANGA, MediaListContentType.POPULAR_MANGA)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for POPULAR_MANHWA`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.MANGA,
                    countryOfOrigin = "KR",
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.MANGA, MediaListContentType.POPULAR_MANHWA)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for POPULAR_NOVEL`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.MANGA,
                    mediaFormat = MediaFormat.NOVEL,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.MANGA, MediaListContentType.POPULAR_NOVEL)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with success response for ONE_SHOT`() =
        runTest {
            // Given
            val mockMedia = Media()
            val mockResponse =
                Page(
                    data = listOf(mockMedia),
                    pageInfo = PageInfo(hasNextPage = true, total = 1),
                )
            val mediaList =
                mockResponse.data.map { media ->
                    MediaListItem.MediaListType(media)
                }

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.MANGA,
                    mediaFormat = MediaFormat.ONE_SHOT,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaList(MediaType.MANGA, MediaListContentType.ONE_SHOT)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(mediaList, mediaListByPage.first())
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaList should update state with error when request fails`() =
        runTest {
            // Given
            val errorMessage = "Network error"
            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns Result.failure(Exception(errorMessage))

            // When
            viewModel.loadMediaList(MediaType.ANIME, MediaListContentType.RECENTLY_UPDATED)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertTrue(mediaListByPage.isEmpty())
                assertEquals(errorMessage, error)
            }
        }

    @Test
    fun `loadMediaListByDay should load media for specific day`() =
        runTest {
            // Given
            val mockSchedule = AiringSchedule(id = 1, media = Media())
            val mockResponse =
                Page(
                    data = listOf(mockSchedule),
                    pageInfo = PageInfo(hasNextPage = false, total = 1),
                )
            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = 1,
                    perPage = 30,
                    airingAtLesser = any(),
                    airingAtGreater = any(),
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.loadMediaListByDay(0)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(mockResponse.pageInfo?.hasNextPage, hasNextPage)
                assertTrue(mediaListByPage[0]?.first() is MediaListItem.ScheduleType)
                assertEquals(null, error)
            }
        }

    @Test
    fun `loadMediaListByDay should update state with error when request fails`() =
        runTest {
            // Given
            val errorMessage = "Network error"
            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = 1,
                    perPage = 30,
                    airingAtLesser = any(),
                    airingAtGreater = any(),
                )
            } returns Result.failure(Exception(errorMessage))

            // When
            viewModel.loadMediaListByDay(0)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertTrue(mediaListByPage.isEmpty())
                assertEquals(errorMessage, error)
            }
        }

    @Test
    fun `page number updates correctly`() {
        // Given
        val initialPage = viewModel.state.value.pageNumber

        // When
        viewModel.increasePageNumber()

        // Then
        assertEquals(initialPage + 1, viewModel.state.value.pageNumber)

        // When
        viewModel.decreasePageNumber()

        // Then
        assertEquals(initialPage, viewModel.state.value.pageNumber)
    }

    @Test
    fun `getSortedCalendarTabs should return tabs sorted relative to current day`() {
        // When
        val sortedTabs = viewModel.getSortedCalendarTabs()

        // Then
        assertEquals(7, sortedTabs.size)
        val today = LocalDateTime.now().dayOfWeek
        assertEquals(today.toCalendarTab(), sortedTabs.first())
    }

    private fun DayOfWeek.toCalendarTab(): CalendarTab =
        when (this) {
            DayOfWeek.SUNDAY -> CalendarTab.SUNDAY
            DayOfWeek.MONDAY -> CalendarTab.MONDAY
            DayOfWeek.TUESDAY -> CalendarTab.TUESDAY
            DayOfWeek.WEDNESDAY -> CalendarTab.WEDNESDAY
            DayOfWeek.THURSDAY -> CalendarTab.THURSDAY
            DayOfWeek.FRIDAY -> CalendarTab.FRIDAY
            DayOfWeek.SATURDAY -> CalendarTab.SATURDAY
        }
}
