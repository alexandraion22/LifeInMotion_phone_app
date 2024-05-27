package com.example.healthapp

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
        icon =  R.drawable.ic_navbar_workout
    )

    object Profile: BottomBarScreen(
        route = "PROFILE",
        icon = R.drawable.ic_navbar_person
    )

    object Steps : BottomBarScreen(
        route = "HOME/STEPS",
        icon = R.drawable.ic_navbar_home
    )
}
