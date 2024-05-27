package com.example.healthapp.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.healthapp.screens.home.AuthScreen
import com.example.healthapp.screens.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        // init navigation
        navigation(route = Graph.AUTHENTICATION, startDestination = "LOGIN") {
            composable(route = "LOGIN") {
                AuthScreen()
            }
        }
        composable(route = Graph.HOME) {
            HomeScreen(navController)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
}