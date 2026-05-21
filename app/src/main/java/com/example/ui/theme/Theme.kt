package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BoldPrimaryDark,
    secondary = BoldSecondaryDark,
    background = BoldBackgroundDark,
    surface = BoldSurfaceDark,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    onBackground = BoldTextLight,
    onSurface = BoldTextLight,
    secondaryContainer = BoldCardHeroDark,
    onSecondaryContainer = BoldTextLight,
    outlineVariant = BoldBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = BoldPrimary,
    secondary = BoldSecondary,
    background = BoldBackground,
    surface = BoldSurface,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = BoldTextDark,
    onSurface = BoldTextDark,
    secondaryContainer = BoldCardHero,
    onSecondaryContainer = BoldTextDark,
    outlineVariant = BoldBorder
)

@Composable
fun MyApplicationTheme(
    themeColor: com.example.viewmodel.ThemeColor = com.example.viewmodel.ThemeColor.FOREST,
    themeMode: com.example.viewmodel.ThemeMode = com.example.viewmodel.ThemeMode.LIGHT,
    content: @Composable () -> Unit,
) {
    val isDark = themeMode == com.example.viewmodel.ThemeMode.DARK
    val colorScheme = when (themeColor) {
        com.example.viewmodel.ThemeColor.FOREST -> {
            if (isDark) {
                darkColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF81C784),
                    secondary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    background = androidx.compose.ui.graphics.Color(0xFF0F1410),
                    surface = androidx.compose.ui.graphics.Color(0xFF161E18),
                    onPrimary = androidx.compose.ui.graphics.Color.Black,
                    onBackground = androidx.compose.ui.graphics.Color(0xFFE8F5E9),
                    onSurface = androidx.compose.ui.graphics.Color(0xFFE8F5E9),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D2920),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8F5E9),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFF2C3E30)
                )
            } else {
                lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                    secondary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    background = androidx.compose.ui.graphics.Color(0xFFF8FAF8),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    onPrimary = androidx.compose.ui.graphics.Color.White,
                    onBackground = androidx.compose.ui.graphics.Color(0xFF191C19),
                    onSurface = androidx.compose.ui.graphics.Color(0xFF191C19),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8F3E0),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF191C19),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFFEFF2EC)
                )
            }
        }
        com.example.viewmodel.ThemeColor.OCEAN -> {
            if (isDark) {
                darkColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
                    secondary = androidx.compose.ui.graphics.Color(0xFF2196F3),
                    background = androidx.compose.ui.graphics.Color(0xFF0D1219),
                    surface = androidx.compose.ui.graphics.Color(0xFF131A24),
                    onPrimary = androidx.compose.ui.graphics.Color.Black,
                    onBackground = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                    onSurface = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF1B2A3A),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFF202E3F)
                )
            } else {
                lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF1976D2),
                    secondary = androidx.compose.ui.graphics.Color(0xFF2196F3),
                    background = androidx.compose.ui.graphics.Color(0xFFF3F6FA),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    onPrimary = androidx.compose.ui.graphics.Color.White,
                    onBackground = androidx.compose.ui.graphics.Color(0xFF121820),
                    onSurface = androidx.compose.ui.graphics.Color(0xFF121820),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF121820),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFFE0E6ED)
                )
            }
        }
        com.example.viewmodel.ThemeColor.LAVENDER -> {
            if (isDark) {
                darkColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFFE1BEE7),
                    secondary = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                    background = androidx.compose.ui.graphics.Color(0xFF150D1A),
                    surface = androidx.compose.ui.graphics.Color(0xFF1F1326),
                    onPrimary = androidx.compose.ui.graphics.Color.Black,
                    onBackground = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                    onSurface = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF321B3C),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFF3A2446)
                )
            } else {
                lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF7B1FA2),
                    secondary = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                    background = androidx.compose.ui.graphics.Color(0xFFFAF6FA),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    onPrimary = androidx.compose.ui.graphics.Color.White,
                    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1220),
                    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1220),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1C1220),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFFEFE8EF)
                )
            }
        }
        com.example.viewmodel.ThemeColor.GOLD -> {
            if (isDark) {
                darkColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFFFFD54F),
                    secondary = androidx.compose.ui.graphics.Color(0xFFFFD54F),
                    background = androidx.compose.ui.graphics.Color(0xFF181611),
                    surface = androidx.compose.ui.graphics.Color(0xFF221F18),
                    onPrimary = androidx.compose.ui.graphics.Color.Black,
                    onBackground = androidx.compose.ui.graphics.Color(0xFFFFF8E5),
                    onSurface = androidx.compose.ui.graphics.Color(0xFFFFF8E5),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF3A3423),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFFFF8E5),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFF463E2A)
                )
            } else {
                lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF9E7A00),
                    secondary = androidx.compose.ui.graphics.Color(0xFFC5A029),
                    background = androidx.compose.ui.graphics.Color(0xFFFAF8F5),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    onPrimary = androidx.compose.ui.graphics.Color.White,
                    onBackground = androidx.compose.ui.graphics.Color(0xFF201D16),
                    onSurface = androidx.compose.ui.graphics.Color(0xFF201D16),
                    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFFFF8E5),
                    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF201D16),
                    outlineVariant = androidx.compose.ui.graphics.Color(0xFFF5F0E5)
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
