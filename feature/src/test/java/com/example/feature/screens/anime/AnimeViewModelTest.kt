package com.example.feature.screens.anime

import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeViewModelTest {
    private lateinit var viewModel: AnimeViewModel
    private lateinit var mediaRepository: MediaRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mediaRepository = mockk()
        viewModel = AnimeViewModel(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadData success scenario updates state correctly`() =
        runTest {
            // Mock successful responses
            val mediaType = MediaType.ANIME
            val trendingNowResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val recentlyUpdatedResponse = Result.success(Page<AiringSchedule>(pageInfo = PageInfo(), data = emptyList()))
            val currentSeasonResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val popularResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val nextSeasonResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))

            coEvery {
                mediaRepository.getTrendingNowMedia(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = mediaType,
                )
            } returns trendingNowResponse

            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(
                    pageNumber = 1,
                    perPage = 20,
                    airingAtLesser = any(),
                    airingAtGreater = 0,
                )
            } returns recentlyUpdatedResponse

            coEvery {
                mediaRepository.getSeasonalMedia(
                    pageNumber = 1,
                    perPage = 20,
                    seasonYear = any(),
                    season = any(),
                    mediaType = mediaType,
                )
            } returnsMany listOf(currentSeasonResponse, nextSeasonResponse)

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = mediaType,
                )
            } returns popularResponse

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify final state
            with(viewModel.state.value) {
                assertEquals(trendingNowResponse.getOrNull()?.data, trendingNowMedia)
                assertEquals(recentlyUpdatedResponse.getOrNull()?.data, recentlyUpdatedMedia)
                assertEquals(currentSeasonResponse.getOrNull()?.data, currentSeasonMedia)
                assertEquals(popularResponse.getOrNull()?.data, popularMedia)
                assertEquals(nextSeasonResponse.getOrNull()?.data, nextSeasonMedia)
                assertEquals(false, isLoading)
                assertNull(error)
            }
        }

    @Test
    fun `loadData failure scenario updates state correctly`() =
        runTest {
            val errorMessage = "Network error"

            // Mock failure response
            coEvery {
                mediaRepository.getTrendingNowMedia(any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(any(), any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            coEvery {
                mediaRepository.getSeasonalMedia(any(), any(), any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            coEvery {
                mediaRepository.getPopularMedia(any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error state
            with(viewModel.state.value) {
                assertNull(trendingNowMedia)
                assertNull(recentlyUpdatedMedia)
                assertNull(currentSeasonMedia)
                assertNull(popularMedia)
                assertNull(nextSeasonMedia)
                assertEquals(false, isLoading)
                assertEquals(errorMessage, error)
            }
        }

    @Test
    fun `loadData partial failure scenario updates state with error`() =
        runTest {
            // Mock mixed responses
            val successResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val errorMessage = "Network error"

            coEvery {
                mediaRepository.getTrendingNowMedia(any(), any(), any())
            } returns successResponse

            coEvery {
                mediaRepository.getRecentlyUpdatedAnimeList(any(), any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            coEvery {
                mediaRepository.getSeasonalMedia(any(), any(), any(), any(), any())
            } returns successResponse

            coEvery {
                mediaRepository.getPopularMedia(any(), any(), any())
            } returns successResponse

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error state (should fail completely if any request fails)
            with(viewModel.state.value) {
                assertNull(trendingNowMedia)
                assertNull(recentlyUpdatedMedia)
                assertNull(currentSeasonMedia)
                assertNull(popularMedia)
                assertNull(nextSeasonMedia)
                assertEquals(false, isLoading)
                assertEquals(errorMessage, error)
            }
        }
}
