package com.example.feature.screens.search

import com.example.core.domain.model.Page
import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
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

@OptIn(ExperimentalCoroutinesApi::class)
class MediaSearchViewModelTest {
    private lateinit var viewModel: MediaSearchViewModel
    private lateinit var mediaRepository: MediaRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mediaRepository = mockk()
        viewModel = MediaSearchViewModel(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadSearchResult should update state with success response`() =
        runTest {
            // Given
            val searchResult =
                Result.success(
                    Page<Media>(
                        pageInfo = PageInfo(hasNextPage = true),
                        data = listOf(Media(idAniList = 1)),
                    ),
                )

            coEvery {
                mediaRepository.getSearchMedia(
                    pageNumber = 1,
                    perPage = 21,
                    mediaType = MediaType.ANIME,
                    search = "test",
                )
            } returns searchResult

            // When
            viewModel.loadSearchResult(
                mediaType = MediaType.ANIME,
                searchQuery = "test",
            )

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertEquals(searchResult.getOrNull()?.pageInfo?.hasNextPage, hasNextPage)
                assertEquals(searchResult.getOrNull()?.data, mediaList)
                assertNull(error)
            }
        }

    @Test
    fun `loadSearchResult should handle empty result`() =
        runTest {
            // Given
            val searchResult =
                Result.success(
                    Page<Media>(
                        pageInfo = PageInfo(hasNextPage = false),
                        data = emptyList(),
                    ),
                )

            coEvery {
                mediaRepository.getSearchMedia(
                    any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(),
                )
            } returns searchResult

            // When
            viewModel.loadSearchResult(
                mediaType = MediaType.ANIME,
                searchQuery = "abcdefg",
            )

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertFalse(hasNextPage)
                assertTrue(mediaList?.isEmpty() == true)
                assertNull(error)
            }
        }

    @Test
    fun `loadSearchResult should handle error response`() =
        runTest {
            // Given
            val errorMessage = "Network error"
            coEvery {
                mediaRepository.getSearchMedia(
                    any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(),
                )
            } returns Result.failure(Exception(errorMessage))

            // When
            viewModel.loadSearchResult(mediaType = MediaType.ANIME)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertFalse(hasNextPage)
                assertEquals(emptyList<Media>(), mediaList)
                assertEquals(errorMessage, error)
            }
        }

    @Test
    fun `loadSearchResult should load more results when requested`() =
        runTest {
            // Given
            val initialMedia = listOf(Media(idAniList = 1))
            val additionalMedia = listOf(Media(idAniList = 2))

            // Set initial state
            coEvery {
                mediaRepository.getSearchMedia(
                    any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(),
                )
            } returns
                Result.success(
                    Page<Media>(
                        data = initialMedia,
                        pageInfo =
                            PageInfo(
                                currentPage = 1,
                                hasNextPage = true,
                                total = 2,
                            ),
                    ),
                )

            viewModel.loadSearchResult(mediaType = MediaType.ANIME)

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Mock load more response
            coEvery {
                mediaRepository.getSearchMedia(
                    any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(),
                )
            } returns
                Result.success(
                    Page<Media>(
                        data = additionalMedia,
                        pageInfo =
                            PageInfo(
                                currentPage = 2,
                                hasNextPage = false,
                                total = 2,
                            ),
                    ),
                )

            // When
            viewModel.incrementPageNumber()
            viewModel.loadSearchResult(
                mediaType = MediaType.ANIME,
                loadMore = true,
            )

            // Advance coroutines
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertFalse(isLoading)
                assertFalse(hasNextPage)
                assertEquals(initialMedia + additionalMedia, mediaList)
                assertNull(error)
                assertEquals(2, pageNumber)
            }
        }

    @Test
    fun `incrementPageNumber should increase page number`() {
        // Given
        val initialPageNumber = viewModel.state.value.pageNumber

        // When
        viewModel.incrementPageNumber()

        // Then
        assertEquals(initialPageNumber + 1, viewModel.state.value.pageNumber)
    }
}
