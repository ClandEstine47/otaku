package com.example.feature.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val result = mediaRepository.getRecentlyUpdatedMedia(
                pageNumber = 1,
                airingTimeInMs = (System.currentTimeMillis() / 1000 - 10000).toInt()
            )

            _state.update { currentState ->
                when {
                    result.isSuccess -> currentState.copy(
                        trendingMedia = result.getOrNull(),
                        isLoading = false,
                        error = null
                    )

                    result.isFailure -> currentState.copy(
                        trendingMedia = null,
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "An unknown error occurred"
                    )

                    else -> currentState.copy(
                        isLoading = false,
                        error = "Unexpected result state"
                    )
                }
            }
        }
    }

    data class AnimeUiState(
        val trendingMedia: List<AiringSchedule>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}