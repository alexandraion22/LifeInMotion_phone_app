package com.example.healthapp.screens.mainscreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.database.bpm.BpmRepository
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.AuthNavGraph

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AuthScreen(
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    startDestination: String,
    bpmRepository: BpmRepository
) {
    AuthNavGraph(navController = navController, userViewModel = userViewModel, startDestination = startDestination, bpmRepository = bpmRepository)
}