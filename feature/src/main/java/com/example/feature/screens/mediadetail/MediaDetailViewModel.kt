package com.example.feature.screens.mediadetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

        private var toggleJob: Job? = null

        fun loadData(id: Int) {
            getMediaDetail(id)
            getMediaThreads(id)
        }

        fun getMediaDetail(
            id: Int,
            fetchFromNetwork: Boolean = false,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoadingMediaDetails = true,
                    )
                }

                val media = mediaRepository.getMediaById(id = id, fetchFromNetwork = fetchFromNetwork)

                _state.update { currentState ->
                    when {
                        media.isSuccess -> {
                            val mediaData = media.getOrNull()
                            currentState.copy(
                                media = mediaData,
                                isFavourite = currentState.isFavourite ?: mediaData?.isFavourite,
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

        fun getMediaThreads(
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

        fun saveMediaListEntry(
            mediaId: Int,
            status: MediaListStatus?,
            score: Double?,
            progress: Int?,
            repeat: Int?,
            private: Boolean?,
            hiddenFromStatusLists: Boolean?,
            startedAt: FuzzyDate?,
            completedAt: FuzzyDate?,
            notes: String?,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isSavingMediaList = true,
                    )
                }

                val result =
                    mediaRepository.saveMediaListEntry(
                        mediaId = mediaId,
                        status = status,
                        score = score,
                        progress = progress,
                        repeat = repeat,
                        private = private,
                        hiddenFromStatusLists = hiddenFromStatusLists,
                        startedAt = startedAt,
                        completedAt = completedAt,
                        notes = notes,
                    )

                _state.update { currentState ->
                    when {
                        result.isSuccess -> {
                            currentState.copy(
                                isSavingMediaList = false,
                                mediaListSaveSuccess = true,
                            )
                        }

                        result.isFailure -> {
                            currentState.copy(
                                isSavingMediaList = false,
                                error = result.exceptionOrNull()?.message ?: "Failed to save media list entry",
                            )
                        }

                        else -> {
                            currentState.copy(
                                isSavingMediaList = false,
                            )
                        }
                    }
                }

                if (result.isSuccess) {
                    getMediaDetail(
                        id = mediaId,
                        fetchFromNetwork = true,
                    )
                }
            }
        }

        fun deleteMediaListEntry(mediaId: Int) {
            val mediaListEntryId =
                state.value.media
                    ?.mediaListEntry
                    ?.id
            if (mediaListEntryId == null) {
                _state.update { it.copy(error = "No media list entry found to delete") }
                return
            }

            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isSavingMediaList = true,
                    )
                }

                val result = mediaRepository.deleteMediaListEntry(mediaListEntryId)

                _state.update { currentState ->
                    when {
                        result.isSuccess -> {
                            currentState.copy(
                                isSavingMediaList = false,
                                mediaListSaveSuccess = true,
                            )
                        }

                        result.isFailure -> {
                            currentState.copy(
                                isSavingMediaList = false,
                                error = result.exceptionOrNull()?.message ?: "Failed to delete media list entry",
                            )
                        }

                        else -> {
                            currentState.copy(
                                isSavingMediaList = false,
                            )
                        }
                    }
                }

                if (result.isSuccess) {
                    getMediaDetail(id = mediaId, fetchFromNetwork = true)
                }
            }
        }

        fun toggleFavourite(mediaId: Int) {
            if (toggleJob?.isActive == true) return
            val media = state.value.media ?: return
            val isAnime = media.type == MediaType.ANIME

            toggleJob =
                viewModelScope.launch {
                    val result =
                        mediaRepository.toggleFavourite(
                            animeId = if (isAnime) mediaId else null,
                            mangaId = if (!isAnime) mediaId else null,
                        )

                    if (result.isSuccess) {
                        _state.update { it.copy(isFavourite = !it.isFavourite!!) }
                    } else {
                        _state.update {
                            it.copy(
                                error = result.exceptionOrNull()?.message ?: "Failed to toggle favourite",
                            )
                        }
                    }
                }
        }
    }
