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

        init {
            viewModelScope.launch {
                loadData()
            }
        }

        fun loadData() {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
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
                        mediaRepository.getRecentlyUpdatedAnimeList(
                            pageNumber = 1,
                            perPage = 20,
                            airingAtLesser = (System.currentTimeMillis() / 1000 - 10000).toInt(),
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
                    when {
                        recentlyUpdatedResult.isSuccess && trendingNowResult.isSuccess && currentSeasonResult.isSuccess && nextSeasonResult.isSuccess && popularResult.isSuccess ->
                            currentState.copy(
                                trendingNowMedia = trendingNowResult.getOrNull()?.data,
                                recentlyUpdatedMedia = recentlyUpdatedResult.getOrNull()?.data,
                                currentSeasonMedia = currentSeasonResult.getOrNull()?.data,
                                popularMedia = popularResult.getOrNull()?.data,
                                nextSeasonMedia = nextSeasonResult.getOrNull()?.data,
                                isLoading = false,
                                error = null,
                            )

                        recentlyUpdatedResult.isFailure || trendingNowResult.isFailure || currentSeasonResult.isFailure || nextSeasonResult.isFailure || popularResult.isFailure ->
                            currentState.copy(
                                trendingNowMedia = null,
                                recentlyUpdatedMedia = null,
                                currentSeasonMedia = null,
                                popularMedia = null,
                                nextSeasonMedia = null,
                                isLoading = false,
                                error = recentlyUpdatedResult.exceptionOrNull()?.message ?: "An unknown error occurred",
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
