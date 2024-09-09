package com.example.feature.medialist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import com.example.feature.Utils.currentAnimeSeason
import com.example.feature.Utils.nextAnimeSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MediaListViewViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val currentTime = LocalDateTime.now()
        private val currentAnimeSeason = currentTime.currentAnimeSeason()
        private val nextAnimeSeason = currentTime.nextAnimeSeason()

        private val _state =
            MutableStateFlow(
                MediaListUiState(),
            )
        val state = _state.asStateFlow()

        fun loadMediaList(
            mediaType: MediaType,
            contentType: MediaListContentType,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val mediaListDeferred =
                    async {
                        when (contentType) {
                            MediaListContentType.RECENTLY_UPDATED -> {
                                mediaRepository.getRecentlyUpdatedAnimeList(
                                    pageNumber = 1,
                                    perPage = 20,
                                    airingTimeInMs = (System.currentTimeMillis() / 1000 - 10000).toInt(),
                                )
                            }
                            MediaListContentType.CURRENT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    seasonYear = currentAnimeSeason.year,
                                    season = currentAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.POPULAR_NOW -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.NEXT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    seasonYear = nextAnimeSeason.year,
                                    season = nextAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.POPULAR_MANGA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    mediaType = mediaType,
                                    countryOfOrigin = "JP",
                                )
                            }
                            MediaListContentType.POPULAR_MANHWA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    mediaType = mediaType,
                                    countryOfOrigin = "KR",
                                )
                            }
                            MediaListContentType.POPULAR_NOVEL -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.NOVEL,
                                )
                            }
                            MediaListContentType.ONE_SHOT -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = 1,
                                    perPage = 20,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.ONE_SHOT,
                                )
                            }
                        }
                    }

                val mediaListResult = mediaListDeferred.await()

                _state.update { currentState ->
                    when {
                        mediaListResult.isSuccess ->
                            currentState.copy(
                                mediaList =
                                    mediaListResult.getOrNull()?.map { media ->
                                        when (media) {
                                            is Media -> {
                                                MediaListItem.MediaListType(media)
                                            }
                                            is AiringSchedule -> {
                                                MediaListItem.ScheduleType(media)
                                            }
                                            else -> {
                                                MediaListItem.MediaListType(Media())
                                            }
                                        }
                                    },
                                isLoading = false,
                                error = null,
                            )

                        mediaListResult.isFailure ->
                            currentState.copy(
                                mediaList = null,
                                isLoading = false,
                                error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                            )

                        else ->
                            currentState.copy(
                                isLoading = false,
                                error = "Unexpected result state",
                            )
                    }
                }
            }
        }
    }
