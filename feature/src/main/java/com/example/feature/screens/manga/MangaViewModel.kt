package com.example.feature.screens.manga

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
                val popularMangaResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                            countryOfOrigin = "JP",
                        )
                    }
                val popularManhwaResultDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
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
                    val hasAnyFailure =
                        trendingNowResult.isFailure ||
                            popularMangaResult.isFailure ||
                            popularManhwaResult.isFailure ||
                            popularNovelResult.isFailure ||
                            popularOneShotResult.isFailure

                    val errorMessage =
                        if (hasAnyFailure) {
                            trendingNowResult.exceptionOrNull()?.message
                                ?: popularMangaResult.exceptionOrNull()?.message
                                ?: popularManhwaResult.exceptionOrNull()?.message
                                ?: popularNovelResult.exceptionOrNull()?.message
                                ?: popularOneShotResult.exceptionOrNull()?.message
                        } else {
                            null
                        }

                    currentState.copy(
                        trendingMangaList = trendingNowResult.getOrNull()?.data ?: currentState.trendingMangaList,
                        popularMangaList = popularMangaResult.getOrNull()?.data ?: currentState.popularMangaList,
                        popularManhwaList = popularManhwaResult.getOrNull()?.data ?: currentState.popularManhwaList,
                        popularNovelList = popularNovelResult.getOrNull()?.data ?: currentState.popularNovelList,
                        popularOneShotList = popularOneShotResult.getOrNull()?.data ?: currentState.popularOneShotList,
                        isLoading = false,
                        error = errorMessage,
                    )
                }
            }
        }
    }
