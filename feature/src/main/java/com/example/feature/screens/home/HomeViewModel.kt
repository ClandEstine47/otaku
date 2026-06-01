package com.example.feature.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.medialistcollection.MediaListSort
import com.example.core.domain.model.user.User
import com.example.core.domain.repository.MainRepository
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val mainRepository: MainRepository,
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val _state =
            MutableStateFlow(
                HomeUiState(),
            )
        val state = _state.asStateFlow()

        init {
            viewModelScope.launch {
                mainRepository.isLoggedIn().collectLatest { isLoggedIn ->
                    if (isLoggedIn) {
                        _state.update { currentState ->
                            currentState.copy(
                                isLoggedIn = true,
                            )
                        }
                        loadHomeData()
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                isLoggedIn = false,
                                isLoading = false,
                                error = null,
                                user = User(),
                                currentAnimeMedia = null,
                                currentMangaMedia = null,
                            )
                        }
                    }
                }
            }
        }

        fun loadHomeData() {
            viewModelScope.launch {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                    )
                }

                val userResult =
                    runCatching {
                        mainRepository.getUserDetails()
                    }

                _state.update { currentState ->
                    currentState.copy(
                        user = userResult.getOrNull() ?: currentState.user,
                    )
                }

                val userIdFromState =
                    state.value.user.id
                        .takeIf { it > 0 }
                val currentAnimeResult =
                    mediaRepository.getAnimeByStatus(
                        pageNumber = 1,
                        perPage = 20,
                        status = MediaListStatus.CURRENT,
                        userId = userIdFromState,
                        sortBy = listOf(MediaListSort.UPDATED_TIME_DESC),
                    )

                val currentMangaResult =
                    mediaRepository.getMangaByStatus(
                        pageNumber = 1,
                        perPage = 20,
                        status = MediaListStatus.CURRENT,
                        userId = userIdFromState,
                        sortBy = listOf(MediaListSort.UPDATED_TIME_DESC),
                    )

                val currentError =
                    userResult.exceptionOrNull()?.message
                        ?: when {
                            currentAnimeResult.isFailure && currentMangaResult.isFailure -> {
                                currentAnimeResult.exceptionOrNull()?.message
                                    ?: currentMangaResult.exceptionOrNull()?.message
                            }

                            else -> {
                                null
                            }
                        }

                _state.update { currentState ->
                    currentState.copy(
                        user = userResult.getOrNull() ?: currentState.user,
                        currentAnimeMedia = currentAnimeResult.getOrNull()?.data,
                        currentMangaMedia = currentMangaResult.getOrNull()?.data,
                        isLoading = false,
                        error = currentError,
                    )
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                mainRepository.removeUserDetails()
            }
        }
    }
