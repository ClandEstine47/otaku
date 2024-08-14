package com.example.feature.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

                val trendingNowResult = trendingNowResultDeferred.await()

                _state.update { currentState ->
                    when {
                        trendingNowResult.isSuccess ->
                            currentState.copy(
                                trendingNowMedia = trendingNowResult.getOrNull(),
                                isLoading = false,
                                error = null,
                            )

                        trendingNowResult.isFailure ->
                            currentState.copy(
                                trendingNowMedia = null,
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
