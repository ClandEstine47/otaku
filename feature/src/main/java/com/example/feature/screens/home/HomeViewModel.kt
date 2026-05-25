package com.example.feature.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MainRepository
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
                        loadUserInfo()
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                isLoggedIn = false,
                            )
                        }
                    }
                }
            }
        }

        fun loadUserInfo() {
            viewModelScope.launch {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                    )
                }

                val userResult = mainRepository.getUserDetails()

                _state.update { currentState ->
                    currentState.copy(
                        user = userResult,
                        isLoading = false,
                        error = null,
                    )
                }
            }
        }
    }
