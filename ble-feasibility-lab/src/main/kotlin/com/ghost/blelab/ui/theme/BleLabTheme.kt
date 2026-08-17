package com.ghost.blelab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = 0xFF90CAF9,
    surface = 0xFF1E1E1E,
    background = 0xFF121212,
    onPrimary = 0xFF000000,
    onSurface = 0xFFFFFFFF,
    onBackground = 0xFFFFFFFF,
)

private val LightColorScheme = lightColorScheme(
    primary = 0xFF1565C0,
    surface = 0xFFFFFFFF,
    background = 0xFFF5F5F5,
    onPrimary = 0xFFFFFFFF,
    onSurface = 0xFF000000,
    onBackground = 0xFF000000,
)

@Composable
fun BleLabTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}