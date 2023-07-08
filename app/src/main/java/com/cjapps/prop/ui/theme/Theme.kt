package com.cjapps.prop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun PropComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val useDynamicColors = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        useDynamicColors -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            val windowsInsetsController = WindowCompat.getInsetsController(window, view)
            windowsInsetsController.isAppearanceLightStatusBars = darkTheme
            windowsInsetsController.isAppearanceLightNavigationBars = darkTheme
        }
    }

    val extendedThemeValues = PropExtendedThemeColors(
        gradientColorList = when {
            useDynamicColors -> listOf(colorScheme.primary, colorScheme.secondary)
            isSystemInDarkTheme() -> listOf(Purple80, Pink80)
            else -> listOf(Purple40, Pink40)
        },
        inverseSecondary = when {
            useDynamicColors -> colorScheme.inversePrimary
            isSystemInDarkTheme() -> PurpleGrey40
            else -> PurpleGrey80
        }
    )
    CompositionLocalProvider(LocalExtendedThemeValues provides extendedThemeValues) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object ThemeDefaults {
    val pagePadding = 16.dp

    val appTitleTextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    )
}

/// Theme extensions
@Immutable
data class PropExtendedThemeColors(
    val gradientColorList: List<Color>,
    val inverseSecondary: Color
)

val LocalExtendedThemeValues = staticCompositionLocalOf {
    PropExtendedThemeColors(
        gradientColorList = listOf(),
        inverseSecondary = Color.LightGray
    )
}

object ExtendedTheme {
    val colors: PropExtendedThemeColors
        @Composable
        get() = LocalExtendedThemeValues.current
}