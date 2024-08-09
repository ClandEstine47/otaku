package com.example.feature.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.AnimeSeason
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.repository.MediaRepository
import com.example.feature.Utils.currentAnimeSeason
import com.example.feature.Utils.nextAnimeSeason
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val now = LocalDateTime.now()

        private val _state =
            MutableStateFlow(
                AnimeUiState(
                    nowAnimeSeason = now.currentAnimeSeason(),
                    nextAnimeSeason = now.nextAnimeSeason(),
                ),
            )
        val state = _state.asStateFlow()

        init {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val currentSeasonResult =
                    mediaRepository.getSeasonalMedia(
                        pageNumber = 1,
                        seasonYear = state.value.nowAnimeSeason.year,
                        season = state.value.nowAnimeSeason.season,
                    )

                val recentlyUpdatedResult =
                    mediaRepository.getRecentlyUpdatedMedia(
                        pageNumber = 1,
                        airingTimeInMs = (System.currentTimeMillis() / 1000 - 10000).toInt(),
                    )

                val trendingNowResult =
                    mediaRepository.getTrendingNowMedia(
                        pageNumber = 1,
                    )

                val nextSeasonResult =
                    mediaRepository.getSeasonalMedia(
                        pageNumber = 1,
                        seasonYear = state.value.nextAnimeSeason.year,
                        season = state.value.nextAnimeSeason.season,
                    )

                val popularResult =
                    mediaRepository.getPopularMedia(
                        pageNumber = 1,
                    )

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

        data class AnimeUiState(
            val currentSeasonMedia: List<Media>? = null,
            val recentlyUpdatedMedia: List<AiringSchedule>? = null,
            val trendingNowMedia: List<Media>? = null,
            val popularMedia: List<Media>? = null,
            val nextSeasonMedia: List<Media>? = null,
            val nowAnimeSeason: AnimeSeason,
            val nextAnimeSeason: AnimeSeason,
            val isLoading: Boolean = false,
            val error: String? = null,
        )
    }
