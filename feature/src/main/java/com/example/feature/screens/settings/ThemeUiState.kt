package com.example.feature.screens.settings

import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppTheme

data class ThemeUiState(
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val selectedColor: AppColor = AppColor.DEFAULT,
    val oledEnabled: Boolean = false,
)
