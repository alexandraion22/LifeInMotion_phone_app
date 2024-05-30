package com.example.healthapp.ui.theme

import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurpleNavbar = Color(0xFFAE79F2)
val PurpleButtons = Color(0xFF761CEA)

@Composable
fun customTextFieldColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    textColor = Color(0xFF333333),
    focusedLabelColor = Color(0xFF333333),
    unfocusedLabelColor = Color(0xFF333333),
    cursorColor = Color(0xFF333333)
)