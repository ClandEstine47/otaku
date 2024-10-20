package com.example.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaSearchViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val _state =
            MutableStateFlow(MediaSearchUiState())
        val state = _state.asStateFlow()

        fun loadSearchResult(
            mediaType: MediaType,
            searchQuery: String? = null,
            season: MediaSeason? = null,
            seasonYear: Int? = null,
            format: MediaFormat? = null,
            status: MediaStatus? = null,
            countryOfOrigin: String? = null,
            genres: List<String>? = null,
            tags: List<String>? = null,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val searchResult =
                    mediaRepository.getSearchMedia(
                        pageNumber = 1,
                        perPage = 20,
                        mediaType = mediaType,
                        search = if (searchQuery?.isBlank() == true) null else searchQuery,
                        season = season,
                        seasonYear = seasonYear,
                        format = format,
                        status = status,
                        countryOfOrigin = countryOfOrigin,
                        genres = genres,
                        tags = tags,
                    )

                _state.update { currentState ->
                    when {
                        searchResult.isSuccess -> {
                            currentState.copy(
                                mediaList = searchResult.getOrNull()?.data,
                                isLoading = false,
                                error = null,
                            )
                        }

                        searchResult.isFailure -> {
                            currentState.copy(
                                mediaList = emptyList(),
                                isLoading = false,
                                error = searchResult.exceptionOrNull()?.message ?: "An unknown error occurred",
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
