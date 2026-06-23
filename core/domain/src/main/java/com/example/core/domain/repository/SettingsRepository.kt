package com.example.core.domain.repository

import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppTheme
import com.example.core.domain.model.settings.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeSettings: Flow<ThemeSettings>

    suspend fun updateTheme(theme: AppTheme)

    suspend fun updateColor(color: AppColor)

    suspend fun updateOled(enabled: Boolean)
}
