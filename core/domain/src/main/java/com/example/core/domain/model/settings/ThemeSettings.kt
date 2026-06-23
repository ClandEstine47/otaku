package com.example.core.domain.model.settings

data class ThemeSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val color: AppColor = AppColor.DEFAULT,
    val oledEnabled: Boolean = false,
)

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM,
}

enum class AppColor {
    DEFAULT,
    DYNAMIC,
    OCEAN,
    FOREST,
    LAVENDER,
}
