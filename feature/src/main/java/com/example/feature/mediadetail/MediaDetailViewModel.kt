package com.example.feature.mediadetail

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

        fun getMediaDetail(id: Int) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val media = mediaRepository.getMediaById(id = id)

                _state.update { currentState ->
                    when {
                        media.isSuccess -> {
                            currentState.copy(
                                media = media.getOrNull(),
                                isLoading = false,
                                error = null,
                            )
                        }

                        media.isFailure -> {
                            currentState.copy(
                                media = null,
                                isLoading = false,
                                error = media.exceptionOrNull()?.message ?: "An unknown error occurred",
                            )
                        }

                        else -> {
                            currentState.copy(
                                isLoading = false,
                                error = "Unexpected result state",
                            )
                        }
                    }
                }
            }
        }
    }
