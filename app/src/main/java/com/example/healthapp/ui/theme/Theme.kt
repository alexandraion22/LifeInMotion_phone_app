package com.example.healthapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.healthapp.R

private val DarkColorPalette = darkColors(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryVariant = PurpleNavbar,
    secondary = PurpleButtons,
    secondaryVariant = Color.White
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryVariant = PurpleNavbar,
    secondary = PurpleButtons,
    secondaryVariant = Color.Black
)

// Define font and typography
val SpaceGrotesk = FontFamily(
    Font(R.font.spacegrotesksemibold)
)
val HealthAppTypography = Typography(
    defaultFontFamily = SpaceGrotesk
)

@Composable
fun HealthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = HealthAppTypography,
        shapes = Shapes,
        content = content
    )
}