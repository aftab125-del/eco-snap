package com.ecosnap.bharat.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NeemGreen,
    onPrimary = SurfaceCard,
    primaryContainer = PaleMint,
    onPrimaryContainer = NeemGreenDark,
    secondary = SuryaAmber,
    onSecondary = SurfaceCard,
    secondaryContainer = SuryaAmberLight,
    onSecondaryContainer = DeepCharcoal,
    tertiary = RupeeGold,
    onTertiary = DeepCharcoal,
    tertiaryContainer = RupeeGoldLight,
    background = KhadiBackground,
    onBackground = DeepCharcoal,
    surface = SurfaceCard,
    onSurface = DeepCharcoal,
    surfaceVariant = PaleMint,
    onSurfaceVariant = SlateGray,
    error = TerracottaRed,
    onError = SurfaceCard
)

@Composable
fun EcoSnapBharatTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = KhadiBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
