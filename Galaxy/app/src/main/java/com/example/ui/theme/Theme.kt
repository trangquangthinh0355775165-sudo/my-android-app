package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SamsungBlueLight,
    onPrimary = Color.White,
    primaryContainer = SamsungBlueDark,
    onPrimaryContainer = Color.White,
    secondary = SamsungTeal,
    onSecondary = Color.Black,
    background = OneUIDarkBg,
    onBackground = OneUIDarkTextPrimary,
    surface = OneUIDarkSurface,
    onSurface = OneUIDarkTextPrimary,
    surfaceVariant = OneUIDarkCard,
    onSurfaceVariant = OneUIDarkTextSecondary,
    outline = OneUIDarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = SamsungBlue,
    onPrimary = Color.White,
    primaryContainer = SamsungBlueLight.copy(alpha = 0.2f),
    onPrimaryContainer = SamsungBlueDark,
    secondary = SamsungTeal,
    onSecondary = Color.White,
    background = OneUILightBg,
    onBackground = OneUILightTextPrimary,
    surface = OneUILightSurface,
    onSurface = OneUILightTextPrimary,
    surfaceVariant = OneUILightCard,
    onSurfaceVariant = OneUILightTextSecondary,
    outline = OneUILightDivider
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
