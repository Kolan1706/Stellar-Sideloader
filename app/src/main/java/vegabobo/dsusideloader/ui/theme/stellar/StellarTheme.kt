package vegabobo.dsusideloader.ui.theme.stellar

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import vegabobo.dsusideloader.ui.theme.Typography

enum class StellarAccent(val displayName: String) {
    TEAL("Teal"),
    CORAL("Coral"),
    PURPLE("Purple"),
    EMERALD("Emerald"),
    BLUE("Blue"),
}

fun StellarAccent.toColor(): Color = when (this) {
    StellarAccent.TEAL -> Teal
    StellarAccent.CORAL -> Coral
    StellarAccent.PURPLE -> Purple
    StellarAccent.EMERALD -> Emerald
    StellarAccent.BLUE -> Blue
}

private fun accentColorScheme(accent: Color, darkTheme: Boolean) = if (darkTheme) {
    darkColorScheme(
        primary = accent,
        secondary = accent.copy(alpha = 0.7f),
        tertiary = Purple,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        outline = DarkCardBorder,
    )
} else {
    lightColorScheme(
        primary = accent,
        secondary = accent.copy(alpha = 0.7f),
        tertiary = Purple,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF0F172A),
        onSurfaceVariant = Color(0xFF475569),
        outline = LightCardBorder,
    )
}

enum class ThemePreset(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SPACE_CUSTOM("Space Custom"),
}

@Composable
fun StellarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: StellarAccent = StellarAccent.TEAL,
    preset: ThemePreset = ThemePreset.SPACE_CUSTOM,
    content: @Composable () -> Unit,
) {
    val effectiveDark = when (preset) {
        ThemePreset.LIGHT -> false
        ThemePreset.DARK -> true
        ThemePreset.SPACE_CUSTOM -> darkTheme
    }
    val colorScheme = accentColorScheme(accent.toColor(), effectiveDark)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !effectiveDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !effectiveDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
