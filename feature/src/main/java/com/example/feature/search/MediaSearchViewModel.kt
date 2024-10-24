package com.example.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
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
            sortBy: MediaSort? = null,
            loadMore: Boolean = false,
        ) {
            viewModelScope.launch {
                if (!loadMore) {
                    _state.update {
                        it.copy(
                            mediaList = emptyList(),
                            pageNumber = 1,
                            isLoading = true,
                        )
                    }
                }

                val searchResult =
                    mediaRepository.getSearchMedia(
                        pageNumber = _state.value.pageNumber,
                        perPage = 21,
                        mediaType = mediaType,
                        search = if (searchQuery?.isBlank() == true) null else searchQuery,
                        season = season,
                        seasonYear = seasonYear,
                        format = format,
                        status = status,
                        countryOfOrigin = countryOfOrigin,
                        genres = genres,
                        tags = tags,
                        sortBy = if (sortBy == null) null else listOf(sortBy),
                    )

                _state.update { currentState ->
                    when {
                        searchResult.isSuccess -> {
                            currentState.copy(
                                mediaList =
                                    if (loadMore) {
                                        (currentState.mediaList ?: emptyList()) + (searchResult.getOrNull()?.data ?: emptyList())
                                    } else {
                                        searchResult.getOrNull()?.data
                                    },
                                isLoading = false,
                                error = null,
                            )
                        }

                        searchResult.isFailure -> {
                            currentState.copy(
                                mediaList = currentState.mediaList ?: emptyList(),
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

        fun incrementPageNumber() {
            _state.update { currentState ->
                currentState.copy(
                    pageNumber = currentState.pageNumber + 1,
                )
            }
        }
    }
