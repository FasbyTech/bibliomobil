package com.fasby.bibliomobil.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = CyberBlack,
    primaryContainer = CyberPurple,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = CyberBlack,
    secondaryContainer = CyberGray,
    onSecondaryContainer = NeonPink,
    tertiary = NeonYellow,
    onTertiary = CyberBlack,
    background = CyberBlack,
    onBackground = OnCyberBackground,
    surface = CyberDark,
    onSurface = OnCyberSurface,
    surfaceVariant = CyberGray,
    onSurfaceVariant = NeonBlue,
    error = CyberError,
    onError = Color.White
)

@Composable
fun BiblioMobilTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = CyberColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
