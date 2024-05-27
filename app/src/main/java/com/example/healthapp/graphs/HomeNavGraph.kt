package com.example.healthapp.graphs

import SettingsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.BottomBarScreen
import com.example.healthapp.screens.HomeContent
import com.example.healthapp.screens.ScreenContent
import com.example.healthapp.screens.StepsContent
import com.example.healthapp.screens.home.AuthScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeContent(navController)
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
            SettingsScreen(navController = navController)
        }
        composable(route = Graph.AUTHENTICATION) {
            AuthScreen()
        }
    }
}