package com.example.feature.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val mediaType = MediaType.MANGA

        private val _state =
            MutableStateFlow(
                MangaUiState(),
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

                val trendingNowResultDeferred =
                    async {
                        mediaRepository.getTrendingNowMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                        )
                    }
                val popularMangaResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                            mediaFormat = MediaFormat.MANGA,
                            countryOfOrigin = "JP",
                        )
                    }
                val popularManhwaResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                            mediaFormat = MediaFormat.MANGA,
                            countryOfOrigin = "KR",
                        )
                    }
                val popularNovelResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                            mediaFormat = MediaFormat.NOVEL,
                        )
                    }
                val popularOneShotResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                            mediaFormat = MediaFormat.ONE_SHOT,
                        )
                    }

                val trendingNowResult = trendingNowResultDeferred.await()
                val popularMangaResult = popularMangaResultDeferred.await()
                val popularManhwaResult = popularManhwaResultDeferred.await()
                val popularNovelResult = popularNovelResultDeferred.await()
                val popularOneShotResult = popularOneShotResultDeferred.await()

                _state.update { currentState ->
                    when {
                        trendingNowResult.isSuccess && popularMangaResult.isSuccess && popularManhwaResult.isSuccess && popularNovelResult.isSuccess && popularOneShotResult.isSuccess ->
                            currentState.copy(
                                trendingMangaList = trendingNowResult.getOrNull(),
                                popularMangaList = popularMangaResult.getOrNull(),
                                popularManhwaList = popularManhwaResult.getOrNull(),
                                popularNovelList = popularNovelResult.getOrNull(),
                                popularOneShotList = popularOneShotResult.getOrNull(),
                                isLoading = false,
                                error = null,
                            )

                        trendingNowResult.isFailure || popularMangaResult.isFailure || popularManhwaResult.isFailure || popularNovelResult.isFailure || popularOneShotResult.isFailure ->
                            currentState.copy(
                                trendingMangaList = null,
                                popularMangaList = null,
                                popularManhwaList = null,
                                popularNovelList = null,
                                popularOneShotList = null,
                                isLoading = false,
                                error = trendingNowResult.exceptionOrNull()?.message ?: "An unknown error occurred",
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
