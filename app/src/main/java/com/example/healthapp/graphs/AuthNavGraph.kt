package com.example.healthapp.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.screens.content.auth.LoginContent
import com.example.healthapp.screens.content.auth.SignUpContent
import com.example.healthapp.screens.content.auth.SignUpDetailsContent
import com.example.healthapp.screens.content.auth.UserViewModel
import com.example.healthapp.screens.mainscreens.HomeScreen


@Composable
fun AuthNavGraph(navController: NavHostController, userViewModel: UserViewModel) {
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
        composable(route = "SIGNUP") {
            SignUpContent(
                navController = navController
            )
        }
        composable(route = "SIGNUP/DETAILS") {
            SignUpDetailsContent(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(route = Graph.HOME) {
            HomeScreen(userViewModel = userViewModel)
        }
    }
}