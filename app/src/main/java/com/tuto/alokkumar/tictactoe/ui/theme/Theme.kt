package com.tuto.alokkumar.tictactoe.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.tuto.alokkumar.tictactoe.data.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = ForegroundDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Black,
    onBackground = ForegroundDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForegroundLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = White,
    onBackground = ForegroundLight,
    onSurface = OnSurfaceLight
)


/**
 * Global application design theme wrapper.
 *
 * Configures colors, typography scales, and shapes. Dynamically updates light/dark scheme based
 * on device options or Material You dynamic color algorithms on Android 12+ (API 31+).
 *
 * @param appTheme Chosen visual scheme mode (Light, Dark, or System default).
 * @param dynamicColor True to enable active Material You wallpaper-derived dynamic colors.
 * @param content Target Composable screen elements to style.
 */
@Composable
fun TicTacToeTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

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
        shapes = Shapes,
        content = content
    )
}