package com.example.healthapp.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.screens.content.auth.LoginContent
import com.example.healthapp.screens.mainscreens.HomeScreen


@Composable
fun AuthNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.AUTHENTICATION,
        startDestination = "LOGIN"
    ) {
        composable(route = "LOGIN") {
            LoginContent(
                navController = navController
            )
        }
        composable(route = Graph.HOME) {
            HomeScreen()
        }
    }
}