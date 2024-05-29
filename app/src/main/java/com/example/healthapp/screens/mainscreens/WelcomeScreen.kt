package com.example.healthapp.screens.mainscreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.graphs.AuthNavGraph
import com.example.healthapp.graphs.WelcomeNavGraph

@Composable
fun WelcomeScreen(navController: NavHostController = rememberNavController()) {
    WelcomeNavGraph(navController = navController)
}