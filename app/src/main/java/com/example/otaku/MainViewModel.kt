package com.example.otaku

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.settings.ThemeSettings
import com.example.core.domain.repository.MainRepository
import com.example.core.domain.repository.SettingsRepository
import com.example.feature.OTAKU_SCHEME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val mainRepository: MainRepository,
        settingsRepository: SettingsRepository,
    ) : ViewModel() {
        val isLoggedIn = mainRepository.isLoggedIn()

        val themeSettings: StateFlow<ThemeSettings> =
            settingsRepository.themeSettings
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = ThemeSettings(),
                )

        fun onIntentDataReceived(data: Uri?) {
            if (data?.scheme == OTAKU_SCHEME) {
                viewModelScope.launch {
                    mainRepository.parseRedirectUri(data)
                }
            }
        }
    }
