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
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF003731),
    background = Color(0xFF0B132B),
    surface = Color(0xFF111C38),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = IndustrialBlue,
    onPrimary = Color.White,
    primaryContainer = IndustrialBlueLight,
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = IndustrialAmber,
    onSecondary = Color.White,
    secondaryContainer = IndustrialAmberLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = IndustrialTeal,
    onTertiary = Color.White,
    tertiaryContainer = IndustrialTealLight,
    onTertiaryContainer = Color(0xFF00201C),
    background = Slate50,
    surface = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300
)

@Composable
fun MecanicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
