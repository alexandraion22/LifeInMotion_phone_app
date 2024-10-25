package com.example.healthapp.ui.theme

import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightPurple = Color(0xFFAE79F2)
val PsychedelicPurple = Color(0xFF761CEA)
val DarkPurple = Color(0xFF58229D)
val CoolGray = Color(0xFF333333)
val VeryLightGray = Color(0XFFF0F0F0)
val KindaLightGray = Color(0xFFE2E8F0)

val DarkColorPalette = darkColors(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryVariant = LightPurple,
    secondary = PsychedelicPurple
)

val LightColorPalette = lightColors(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryVariant = LightPurple,
    secondary = PsychedelicPurple
)

@Composable
fun customTextFieldColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    textColor = CoolGray,
    focusedLabelColor = CoolGray,
    unfocusedLabelColor = CoolGray,
    cursorColor = CoolGray,
)