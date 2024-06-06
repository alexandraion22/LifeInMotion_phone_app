package com.example.healthapp.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.healthapp.BottomBarScreen
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.screens.mainscreens.AuthScreen
import com.example.healthapp.screens.mainscreens.HomeScreen
import com.example.healthapp.screens.mainscreens.WelcomeScreen

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    userViewModel: UserViewModel
) {
    val validStartDestinations = setOf(
        Graph.AUTHENTICATION,
        Graph.HOME,
        Graph.WELCOME
    )

    if (startDestination !in validStartDestinations) {
        Log.e("NavigationError", "Invalid start destination: $startDestination")
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication graph
        navigation(route = Graph.AUTHENTICATION, startDestination = "LOGIN") {
            composable(route = "LOGIN") {
                AuthScreen(userViewModel = userViewModel)
            }
        }
        // Home graph
        navigation(route = Graph.HOME, startDestination = BottomBarScreen.Home.route) {
            composable(route = BottomBarScreen.Home.route) {
                HomeScreen(userViewModel = userViewModel)
            }
        }
        navigation(route = Graph.WELCOME, startDestination = "WELCOME") {
            composable(route = "WELCOME") {
                WelcomeScreen()
            }
        }
    }
}


object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val WELCOME = "welcome_graph"
}