package com.example.feature.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MainRepository
import com.example.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val mainRepository: MainRepository,
    ) : ViewModel() {
        val notificationsEnabled: StateFlow<Boolean> =
            settingsRepository.appSettings
                .map { it.notificationsEnabled }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = true,
                )

        val isLoggedIn: StateFlow<Boolean> =
            mainRepository
                .isLoggedIn()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = false,
                )

        fun onNotificationsToggled(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.updateNotificationsEnabled(enabled)
            }
        }
    }
