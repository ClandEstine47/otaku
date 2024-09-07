package com.example.feature.medialist

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
class MediaListViewViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val mediaType = MediaType.ANIME

        private val _state =
            MutableStateFlow(
                MediaListUiState(),
            )
        val state = _state.asStateFlow()

        init {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val mediaListDeferred =
                    async {
                        mediaRepository.getPopularMedia(
                            pageNumber = 1,
                            perPage = 20,
                            mediaType = mediaType,
                        )
                    }

                val mediaListResult = mediaListDeferred.await()

                _state.update { currentState ->
                    when {
                        mediaListResult.isSuccess ->
                            currentState.copy(
                                mediaList = mediaListResult.getOrNull()?.map { MediaListItem.MediaListType(it) },
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
