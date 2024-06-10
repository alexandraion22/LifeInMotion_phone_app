package com.example.healthapp.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.screens.mainscreens.BottomBarScreen
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.screens.mainscreens.AuthScreen
import com.example.healthapp.screens.mainscreens.HomeScreen
import com.example.healthapp.screens.mainscreens.WelcomeScreen

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    startRoute: String,
    startDestinationPage: String,
    userViewModel: UserViewModel,
    bpmDailyRepository: BpmDailyRepository,
    bpmHourlyRepository: BpmHourlyRepository
) {
    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        // Authentication graph
        navigation(route = Graph.AUTHENTICATION, startDestination = startDestinationPage) {
            composable(route = startDestinationPage ) {
                AuthScreen(userViewModel = userViewModel, startDestination = startDestinationPage, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository)
            }

        }
        // Home graph
        navigation(route = Graph.HOME, startDestination = BottomBarScreen.Home.route) {
            composable(route = BottomBarScreen.Home.route) {
                HomeScreen(userViewModel = userViewModel, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository)
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