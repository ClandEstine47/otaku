package com.example.feature.screens.mediadetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val _state =
            MutableStateFlow(
                MediaDetailUiState(),
            )
        val state = _state.asStateFlow()

        fun loadData(id: Int) {
            getMediaDetail(id)
            getMediaThreads(id)
        }

        private fun getMediaDetail(id: Int) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoadingMediaDetails = true,
                    )
                }

                val media = mediaRepository.getMediaById(id = id)

                _state.update { currentState ->
                    when {
                        media.isSuccess -> {
                            currentState.copy(
                                media = media.getOrNull(),
                                isLoadingMediaDetails = false,
                                error = null,
                            )
                        }

                        media.isFailure -> {
                            currentState.copy(
                                media = null,
                                isLoadingMediaDetails = false,
                                error = media.exceptionOrNull()?.message ?: "An unknown error occurred",
                            )
                        }

                        else -> {
                            currentState.copy(
                                isLoadingMediaDetails = false,
                                error = "Unexpected result state",
                            )
                        }
                    }
                }
            }
        }

        private fun getMediaThreads(
            mediaId: Int,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoadingMediaThreads = true,
                    )
                }

                val threads =
                    mediaRepository.getMediaThreads(
                        pageNumber = 1,
                        perPage = 5,
                        mediaId = mediaId,
                    )

                _state.update { currentState ->
                    when {
                        threads.isSuccess -> {
                            currentState.copy(
                                mediaThreads = threads.getOrNull()?.data,
                                isLoadingMediaThreads = false,
                                error = null,
                            )
                        }

                        threads.isFailure -> {
                            currentState.copy(
                                mediaThreads = null,
                                isLoadingMediaThreads = false,
                                error = threads.exceptionOrNull()?.message ?: "An unknown error occurred",
                            )
                        }

                        else -> {
                            currentState.copy(
                                isLoadingMediaThreads = false,
                                error = "Unexpected result state",
                            )
                        }
                    }
                }
            }
        }
    }
