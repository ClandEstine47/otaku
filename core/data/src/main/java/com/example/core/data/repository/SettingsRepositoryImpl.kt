package com.example.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.core.data.di.DataModule.getValue
import com.example.core.data.di.DataModule.setValue
import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppTheme
import com.example.core.domain.model.settings.ThemeSettings
import com.example.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : SettingsRepository {
        override val themeSettings: Flow<ThemeSettings> =
            dataStore.data.map { preferences ->
                ThemeSettings(
                    theme = preferences[THEME_MODE_KEY]?.let { AppTheme.valueOf(it) } ?: AppTheme.SYSTEM,
                    color = preferences[THEME_COLOR_KEY]?.let { AppColor.valueOf(it) } ?: AppColor.DEFAULT,
                    oledEnabled = preferences[OLED_ENABLED_KEY] ?: false,
                )
            }

        override suspend fun updateTheme(theme: AppTheme) {
            dataStore.setValue(THEME_MODE_KEY, theme.name)
        }

        override suspend fun updateColor(color: AppColor) {
            dataStore.setValue(THEME_COLOR_KEY, color.name)
        }

        override suspend fun updateOled(enabled: Boolean) {
            dataStore.setValue(OLED_ENABLED_KEY, enabled)
        }

        companion object {
            private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
            private val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
            private val OLED_ENABLED_KEY = booleanPreferencesKey("oled_enabled")
        }
    }
