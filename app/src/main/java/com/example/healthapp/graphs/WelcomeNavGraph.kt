package com.example.healthapp.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.screens.content.welcome.WelcomeContent

@Composable
fun WelcomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.WELCOME,
        startDestination = "WELCOME"
    ) {
        composable(route = "WELCOME") {
            WelcomeContent()
        }
    }
}