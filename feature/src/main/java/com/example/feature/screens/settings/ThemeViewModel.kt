package com.example.feature.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppTheme
import com.example.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        val uiState: StateFlow<ThemeUiState> =
            settingsRepository.appSettings
                .map { settings ->
                    ThemeUiState(
                        selectedTheme = settings.theme,
                        selectedColor = settings.color,
                        oledEnabled = settings.oledEnabled,
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = ThemeUiState(),
                )

        fun onThemeSelected(theme: AppTheme) {
            viewModelScope.launch {
                settingsRepository.updateTheme(theme)
            }
        }

        fun onColorSelected(color: AppColor) {
            viewModelScope.launch {
                settingsRepository.updateColor(color)
            }
        }

        fun onOledChanged(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.updateOled(enabled)
            }
        }
    }
