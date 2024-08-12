package com.example.feature.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

        private suspend fun loadData() {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val currentSeasonDeferred =
                    async {
                        mediaRepository.getSeasonalMedia(
                            pageNumber = 1,
                            seasonYear = state.value.nowAnimeSeason.year,
                            season = state.value.nowAnimeSeason.season,
                        )
                    }
                val recentlyUpdatedResultDeferred =
                    async {
                        mediaRepository.getRecentlyUpdatedMedia(
                            pageNumber = 1,
                            airingTimeInMs = (System.currentTimeMillis() / 1000 - 10000).toInt(),
                        )
                    }
                val trendingNowResultDeferred =
                    async {
                        mediaRepository.getTrendingNowMedia(
                            pageNumber = 1,
                        )
                    }
                val nextSeasonResultDeferred =
                    async {
                        mediaRepository.getSeasonalMedia(
                            pageNumber = 1,
                            seasonYear = state.value.nextAnimeSeason.year,
                            season = state.value.nextAnimeSeason.season,
                        )
                    }
                val popularResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                        )
                    }

                val currentSeasonResult = currentSeasonDeferred.await()
                val recentlyUpdatedResult = recentlyUpdatedResultDeferred.await()
                val trendingNowResult = trendingNowResultDeferred.await()
                val nextSeasonResult = nextSeasonResultDeferred.await()
                val popularResult = popularResultDeferred.await()

                _state.update { currentState ->
                    when {
                        recentlyUpdatedResult.isSuccess && trendingNowResult.isSuccess && currentSeasonResult.isSuccess && nextSeasonResult.isSuccess && popularResult.isSuccess ->
                            currentState.copy(
                                currentSeasonMedia = currentSeasonResult.getOrNull(),
                                recentlyUpdatedMedia = recentlyUpdatedResult.getOrNull(),
                                trendingNowMedia = trendingNowResult.getOrNull(),
                                nextSeasonMedia = nextSeasonResult.getOrNull(),
                                popularMedia = popularResult.getOrNull(),
                                isLoading = false,
                                error = null,
                            )

                        recentlyUpdatedResult.isFailure || trendingNowResult.isFailure || currentSeasonResult.isFailure || nextSeasonResult.isFailure || popularResult.isFailure ->
                            currentState.copy(
                                currentSeasonMedia = null,
                                recentlyUpdatedMedia = null,
                                trendingNowMedia = null,
                                nextSeasonMedia = null,
                                popularMedia = null,
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
