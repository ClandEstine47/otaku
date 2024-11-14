package com.example.feature.screens.mediadetail

import com.example.core.domain.model.Page
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.repository.MediaRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaDetailViewModelTest {
    private lateinit var viewModel: MediaDetailViewModel
    private lateinit var mediaRepository: MediaRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mediaRepository = mockk()
        viewModel = MediaDetailViewModel(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state is correct`() {
        with(viewModel.state.value) {
            assertNull(media)
            assertNull(mediaThreads)
            assertFalse(isLoadingMediaDetails)
            assertFalse(isLoadingMediaThreads)
            assertNull(error)
        }
    }

    @Test
    fun `getMediaDetail success updates state correctly`() =
        runTest {
            // Given
            val mediaId = 1
            val mockMedia = mockk<Media>()
            coEvery { mediaRepository.getMediaById(mediaId) } returns Result.success(mockMedia)

            // When
            viewModel.getMediaDetail(mediaId)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertEquals(mockMedia, media)
                assertFalse(isLoadingMediaDetails)
                assertNull(error)
            }

            coVerify(exactly = 1) { mediaRepository.getMediaById(mediaId) }
        }

    @Test
    fun `getMediaDetail failure updates state with error`() =
        runTest {
            // Given
            val mediaId = 1
            val errorMessage = "Network error"
            coEvery {
                mediaRepository.getMediaById(mediaId)
            } returns Result.failure(Exception(errorMessage))

            // When
            viewModel.getMediaDetail(mediaId)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertNull(media)
                assertFalse(isLoadingMediaDetails)
                assertEquals(errorMessage, error)
            }

            coVerify(exactly = 1) { mediaRepository.getMediaById(mediaId) }
        }

    @Test
    fun `getMediaThreads success updates state correctly`() =
        runTest {
            // Given
            val mediaId = 1
            val mockThreads = mockk<List<Thread>>()
            val mockResponse =
                mockk<Page<Thread>> {
                    every { data } returns mockThreads
                }

            coEvery {
                mediaRepository.getMediaThreads(
                    pageNumber = 1,
                    perPage = 5,
                    mediaId = mediaId,
                )
            } returns Result.success(mockResponse)

            // When
            viewModel.getMediaThreads(mediaId)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertEquals(mockThreads, mediaThreads)
                assertFalse(isLoadingMediaThreads)
                assertNull(error)
            }

            coVerify(exactly = 1) {
                mediaRepository.getMediaThreads(
                    pageNumber = 1,
                    perPage = 5,
                    mediaId = mediaId,
                )
            }
        }

    @Test
    fun `getMediaThreads failure updates state with error`() =
        runTest {
            // Given
            val mediaId = 1
            val errorMessage = "Network error"
            coEvery {
                mediaRepository.getMediaThreads(any(), any(), any())
            } returns Result.failure(Exception(errorMessage))

            // When
            viewModel.getMediaThreads(mediaId)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            with(viewModel.state.value) {
                assertNull(mediaThreads)
                assertFalse(isLoadingMediaThreads)
                assertEquals(errorMessage, error)
            }

            coVerify(exactly = 1) {
                mediaRepository.getMediaThreads(
                    pageNumber = 1,
                    perPage = 5,
                    mediaId = mediaId,
                )
            }
        }

    @Test
    fun `loading state is updated correctly during getMediaDetail`() =
        runTest {
            // Given
            val mediaId = 1
            coEvery {
                mediaRepository.getMediaById(mediaId)
            } coAnswers {
                delay(100)
                Result.success(mockk())
            }

            // When
            viewModel.getMediaDetail(mediaId)
            testDispatcher.scheduler.advanceTimeBy(10)

            // Then - verify loading state is true initially
            assertEquals(true, viewModel.state.value.isLoadingMediaDetails)

            // When - complete the operation
            testDispatcher.scheduler.advanceTimeBy(200)

            // Then - verify loading state is false after completion
            assertEquals(false, viewModel.state.value.isLoadingMediaDetails)
        }

    @Test
    fun `loading state is updated correctly during getMediaThreads`() =
        runTest {
            // Given
            val mediaId = 1
            coEvery {
                mediaRepository.getMediaThreads(any(), any(), any())
            } coAnswers {
                delay(100)
                Result.success(
                    mockk<Page<Thread>> {
                        every { data } returns mockk()
                    },
                )
            }

            // When
            viewModel.getMediaThreads(mediaId)
            testDispatcher.scheduler.advanceTimeBy(10)

            // Then - verify loading state is true initially
            assertEquals(true, viewModel.state.value.isLoadingMediaThreads)

            // When - complete the operation
            testDispatcher.scheduler.advanceTimeBy(200)

            // Then - verify loading state is false after completion
            assertEquals(false, viewModel.state.value.isLoadingMediaThreads)
        }

    @Test
    fun `multiple operations maintain independent loading states`() =
        runTest {
            // Given
            val mediaId = 1
            coEvery {
                mediaRepository.getMediaById(mediaId)
            } coAnswers {
                delay(100)
                Result.success(mockk())
            }
            coEvery {
                mediaRepository.getMediaThreads(any(), any(), any())
            } coAnswers {
                delay(200)
                Result.success(
                    mockk<Page<Thread>> {
                        every { data } returns mockk()
                    },
                )
            }

            // When
            viewModel.getMediaDetail(mediaId)
            viewModel.getMediaThreads(mediaId)

            testDispatcher.scheduler.advanceTimeBy(10)

            // Then - verify both loading states are true initially
            with(viewModel.state.value) {
                assertEquals(true, isLoadingMediaDetails)
                assertEquals(true, isLoadingMediaThreads)
            }

            // When - advance time to complete first operation
            testDispatcher.scheduler.advanceTimeBy(150)

            // Then - verify only media details loading is complete
            with(viewModel.state.value) {
                assertEquals(false, isLoadingMediaDetails)
                assertEquals(true, isLoadingMediaThreads)
            }

            // When - advance time to complete second operation
            testDispatcher.scheduler.advanceTimeBy(100)

            // Then - verify both operations are complete
            with(viewModel.state.value) {
                assertEquals(false, isLoadingMediaDetails)
                assertEquals(false, isLoadingMediaThreads)
            }
        }
}
