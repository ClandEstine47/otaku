package com.example.feature.search

import androidx.lifecycle.ViewModel
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    }
