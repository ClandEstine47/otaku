package com.example.core.domain.repository

import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppSettings
import com.example.core.domain.model.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val appSettings: Flow<AppSettings>

    suspend fun updateTheme(theme: AppTheme)

    suspend fun updateColor(color: AppColor)

    suspend fun updateOled(enabled: Boolean)

    suspend fun updateNotificationsEnabled(enabled: Boolean)
}
