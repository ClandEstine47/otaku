package com.example.feature.screens.manga

import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
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
import kotlin.Result

@OptIn(ExperimentalCoroutinesApi::class)
class MangaViewModelTest {
    private lateinit var viewModel: MangaViewModel
    private lateinit var mediaRepository: MediaRepository
    private lateinit var mediaType: MediaType
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mediaRepository = mockk()
        viewModel = MangaViewModel(mediaRepository)
        mediaType = MediaType.MANGA
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
            val trendingNowResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val popularMangaResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val popularManhwaResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val popularNovelResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))
            val popularOneShotResponse = Result.success(Page<Media>(pageInfo = PageInfo(), data = emptyList()))

            coEvery {
                mediaRepository.getTrendingNowMedia(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = mediaType,
                )
            } returns trendingNowResponse

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = mediaType,
                    countryOfOrigin = any(),
                    mediaFormat = any(),
                )
            } returnsMany listOf(popularMangaResponse, popularManhwaResponse, popularNovelResponse, popularOneShotResponse)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify final state
            with(viewModel.state.value) {
                assertEquals(trendingNowResponse.getOrNull()?.data, trendingMangaList)
                assertEquals(popularMangaResponse.getOrNull()?.data, popularMangaList)
                assertEquals(popularManhwaResponse.getOrNull()?.data, popularManhwaList)
                assertEquals(popularNovelResponse.getOrNull()?.data, popularNovelList)
                assertEquals(popularOneShotResponse.getOrNull()?.data, popularOneShotList)
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

            // Mock failure response
            coEvery {
                mediaRepository.getPopularMedia(any(), any(), any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error state
            with(viewModel.state.value) {
                assertNull(trendingMangaList)
                assertNull(popularMangaList)
                assertNull(popularManhwaList)
                assertNull(popularNovelList)
                assertNull(popularOneShotList)
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
            } returns Result.failure(Exception(errorMessage))

            coEvery {
                mediaRepository.getPopularMedia(
                    pageNumber = 1,
                    perPage = 20,
                    mediaType = mediaType,
                    countryOfOrigin = any(),
                    mediaFormat = any(),
                )
            } returns successResponse

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error state (should fail completely if any request fails)
            with(viewModel.state.value) {
                assertNull(trendingMangaList)
                assertNull(popularMangaList)
                assertNull(popularManhwaList)
                assertNull(popularNovelList)
                assertNull(popularOneShotList)
                assertEquals(false, isLoading)
                assertEquals(errorMessage, error)
            }
        }
}
