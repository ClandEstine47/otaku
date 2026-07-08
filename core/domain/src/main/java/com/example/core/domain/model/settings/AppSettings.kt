package com.example.core.domain.model.settings

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val color: AppColor = AppColor.DEFAULT,
    val oledEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
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
