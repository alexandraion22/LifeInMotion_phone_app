package com.example.healthapp.graphs

import SettingsContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.BottomBarScreen
import com.example.healthapp.screens.content.auth.UserViewModel
import com.example.healthapp.screens.content.home.HomeContent
import com.example.healthapp.screens.content.home.ScreenContent
import com.example.healthapp.screens.content.home.StepsContent
import com.example.healthapp.screens.mainscreens.AuthScreen

@Composable
fun HomeNavGraph(navController: NavHostController, userViewModel: UserViewModel) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeContent(navController = navController, userViewModel = userViewModel)
        }
        composable(route = BottomBarScreen.Sleep.route) {
            ScreenContent(
                name = BottomBarScreen.Sleep.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Workout.route) {
            ScreenContent(
                name = BottomBarScreen.Workout.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Profile.route) {
            ScreenContent(
                name = BottomBarScreen.Profile.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Steps.route) {
            StepsContent()
        }
        composable(route = "SETTINGS") {
            SettingsContent(navController = navController)
        }
        composable(route = Graph.AUTHENTICATION) {
            AuthScreen(userViewModel = userViewModel)
        }
    }
}