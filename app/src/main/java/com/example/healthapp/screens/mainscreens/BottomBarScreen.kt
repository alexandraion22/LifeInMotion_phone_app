package com.example.healthapp.screens.mainscreens

import com.example.healthapp.R

sealed class BottomBarScreen(
    val route: String,
    val icon: Int
) {
    object Home : BottomBarScreen(
        route = "HOME",
        icon = R.drawable.ic_navbar_home
    )

    object Sleep: BottomBarScreen(
        route = "SLEEP",
        icon = R.drawable.ic_navbar_sleep
    )

    object Workout: BottomBarScreen(
        route = "WORKOUT",
        icon = R.drawable.ic_navbar_workout
    )

    object Profile: BottomBarScreen(
        route = "PROFILE",
        icon = R.drawable.ic_navbar_person
    )

    object Steps : BottomBarScreen(
        route = "HOME/STEPS",
        icon = R.drawable.ic_navbar_home
    )

    object Bpm : BottomBarScreen(
        route = "HOME/BPM",
        icon = R.drawable.ic_navbar_home
    )

    object ProfileSettings : BottomBarScreen(
        route = "PROFILE/SETTINGS",
        icon = R.drawable.ic_navbar_home
    )
}
