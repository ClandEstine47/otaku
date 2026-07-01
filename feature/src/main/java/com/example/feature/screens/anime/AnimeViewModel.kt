package com.example.feature.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AnimeViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val currentTime = LocalDateTime.now()
        private val mediaType = MediaType.ANIME

        private val _state =
            MutableStateFlow(
                AnimeUiState(
                    nowAnimeSeason = currentTime.currentAnimeSeason(),
                    nextAnimeSeason = currentTime.nextAnimeSeason(),
                ),
            )
        val state = _state.asStateFlow()

        fun loadData() {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                    )
                }

                val trendingNowResultDeferred =
                    async {
                        mediaRepository.getTrendingNowMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                        )
                    }
                val recentlyUpdatedResultDeferred =
                    async {
                        // Stabilize timestamp to nearest 10 minutes (600 seconds) for better caching
                        val stableTimestamp = (System.currentTimeMillis() / 1000 / 600) * 600
                        mediaRepository.getRecentlyUpdatedAnimeList(
                            pageNumber = 1,
                            perPage = 20,
                            airingAtLesser = (stableTimestamp - 10000).toInt(),
                            airingAtGreater = 0,
                        )
                    }
                val currentSeasonDeferred =
                    async {
                        mediaRepository.getSeasonalMedia(
                            pageNumber = 1,
                            perPage = 20,
                            seasonYear = state.value.nowAnimeSeason.year,
                            season = state.value.nowAnimeSeason.season,
                            mediaType = mediaType,
                        )
                    }
                val popularResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                        )
                    }
                val nextSeasonResultDeferred =
                    async {
                        mediaRepository.getSeasonalMedia(
                            pageNumber = 1,
                            perPage = 20,
                            seasonYear = state.value.nextAnimeSeason.year,
                            season = state.value.nextAnimeSeason.season,
                            mediaType = mediaType,
                        )
                    }

                val trendingNowResult = trendingNowResultDeferred.await()
                val recentlyUpdatedResult = recentlyUpdatedResultDeferred.await()
                val currentSeasonResult = currentSeasonDeferred.await()
                val popularResult = popularResultDeferred.await()
                val nextSeasonResult = nextSeasonResultDeferred.await()

                _state.update { currentState ->
                    val hasAnyFailure =
                        trendingNowResult.isFailure ||
                            recentlyUpdatedResult.isFailure ||
                            currentSeasonResult.isFailure ||
                            popularResult.isFailure ||
                            nextSeasonResult.isFailure

                    val errorMessage =
                        if (hasAnyFailure) {
                            trendingNowResult.exceptionOrNull()?.message
                                ?: recentlyUpdatedResult.exceptionOrNull()?.message
                                ?: currentSeasonResult.exceptionOrNull()?.message
                                ?: popularResult.exceptionOrNull()?.message
                                ?: nextSeasonResult.exceptionOrNull()?.message
                        } else {
                            null
                        }

                    currentState.copy(
                        trendingNowMedia = trendingNowResult.getOrNull()?.data ?: currentState.trendingNowMedia,
                        recentlyUpdatedMedia = recentlyUpdatedResult.getOrNull()?.data ?: currentState.recentlyUpdatedMedia,
                        currentSeasonMedia = currentSeasonResult.getOrNull()?.data ?: currentState.currentSeasonMedia,
                        popularMedia = popularResult.getOrNull()?.data ?: currentState.popularMedia,
                        nextSeasonMedia = nextSeasonResult.getOrNull()?.data ?: currentState.nextSeasonMedia,
                        isLoading = false,
                        error = errorMessage,
                    )
                }
            }
        }
    }
