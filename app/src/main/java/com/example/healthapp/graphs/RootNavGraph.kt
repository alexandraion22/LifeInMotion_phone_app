package com.example.healthapp.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.healthapp.BottomBarScreen
import com.example.healthapp.screens.home.AuthScreen
import com.example.healthapp.screens.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController, isUserAuthenticated: Boolean) {
    Log.d("TAG","2")
    NavHost(
        navController = navController,
        startDestination = if (isUserAuthenticated) Graph.HOME else Graph.AUTHENTICATION
    ) {
        // Authentication graph
        navigation(route = Graph.AUTHENTICATION, startDestination = "LOGIN") {
            composable(route = "LOGIN") {
                AuthScreen()
            }
        }
        // Home graph
        navigation(route = Graph.HOME, startDestination = BottomBarScreen.Home.route) {
            composable(route = BottomBarScreen.Home.route) {
                HomeScreen()
            }
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
}
