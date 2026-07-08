package com.example.otaku.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppSettings
import com.example.core.domain.model.settings.AppTheme

private val DarkColorScheme =
    darkColorScheme(
        primary = dark_primary,
        secondary = dark_secondary,
        tertiary = Pink80,
        background = dark_background,
        onBackground = dark_on_background,
        primaryContainer = Color(0xFF4F378B),
        onPrimaryContainer = Color(0xFFEADDFF),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = light_primary,
        secondary = light_secondary,
        tertiary = Pink40,
        background = light_background,
        onBackground = light_on_background,
        primaryContainer = Color(0xFFEADDFF),
        onPrimaryContainer = Color(0xFF21005D),
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
     */
    )

private val OceanDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF4FC3F7),
        secondary = Color(0xFF0288D1),
        background = Color(0xFF011627),
        onBackground = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF004D60),
        onPrimaryContainer = Color(0xFFB1ECFF),
    )

private val OceanLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF0288D1),
        secondary = Color(0xFF4FC3F7),
        background = Color(0xFFE1F5FE),
        onBackground = Color(0xFF011627),
        primaryContainer = Color(0xFFB1ECFF),
        onPrimaryContainer = Color(0xFF001F28),
    )

private val ForestDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF81C784),
        secondary = Color(0xFF388E3C),
        background = Color(0xFF1B2E1C),
        onBackground = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF1E4E20),
        onPrimaryContainer = Color(0xFFB9F6BC),
    )

private val ForestLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF388E3C),
        secondary = Color(0xFF81C784),
        background = Color(0xFFE8F5E9),
        onBackground = Color(0xFF1B2E1C),
        primaryContainer = Color(0xFFB9F6BC),
        onPrimaryContainer = Color(0xFF002105),
    )

private val LavenderDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFB39DDB),
        secondary = Color(0xFF673AB7),
        background = Color(0xFF1A1625),
        onBackground = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF4A3F6B),
        onPrimaryContainer = Color(0xFFE9DDFF),
    )

private val LavenderLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF673AB7),
        secondary = Color(0xFFB39DDB),
        background = Color(0xFFF3E5F5),
        onBackground = Color(0xFF1A1625),
        primaryContainer = Color(0xFFE9DDFF),
        onPrimaryContainer = Color(0xFF21005E),
    )

@Composable
fun OtakuTheme(
    settings: AppSettings = AppSettings(),
    content: @Composable () -> Unit,
) {
    val darkTheme =
        when (settings.theme) {
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
            AppTheme.SYSTEM -> isSystemInDarkTheme()
        }

    val dynamicColor = settings.color == AppColor.DYNAMIC

    var colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            else -> {
                when (settings.color) {
                    AppColor.OCEAN -> if (darkTheme) OceanDarkColorScheme else OceanLightColorScheme
                    AppColor.FOREST -> if (darkTheme) ForestDarkColorScheme else ForestLightColorScheme
                    AppColor.LAVENDER -> if (darkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
                    else -> if (darkTheme) DarkColorScheme else LightColorScheme
                }
            }
        }

    if (darkTheme && settings.oledEnabled) {
        colorScheme =
            colorScheme.copy(
                background = Color.Black,
                surface = Color.Black,
                surfaceVariant = Color(0xFF121212),
            )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
