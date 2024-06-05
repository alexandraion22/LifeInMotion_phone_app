package com.example.healthapp.screens.mainscreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.AuthNavGraph

@Composable
fun AuthScreen(
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel
) {
    AuthNavGraph(navController = navController, userViewModel = userViewModel)
}