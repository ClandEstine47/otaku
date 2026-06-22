package com.example.feature.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ProfileUiState())
        val state: StateFlow<ProfileUiState> = _state.asStateFlow()

        private val userId: Int? = savedStateHandle["userId"]

        init {
            userId?.let { loadUserProfile(it) } ?: run {
                _state.update { it.copy(error = "User ID not found") }
            }
        }

        fun loadUserProfile(userId: Int) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                mediaRepository
                    .getUser(userId)
                    .onSuccess { user ->
                        _state.update { it.copy(user = user, isLoading = false) }
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message, isLoading = false) }
                    }
            }
        }
    }
