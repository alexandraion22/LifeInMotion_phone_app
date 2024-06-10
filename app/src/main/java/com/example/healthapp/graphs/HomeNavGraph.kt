package com.example.healthapp.graphs

import SettingsContent
import BpmContent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.screens.mainscreens.BottomBarScreen
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.screens.content.home.HomeContent
import com.example.healthapp.screens.content.home.ProfileContent
import com.example.healthapp.screens.content.home.ProfileSettingsContent
import com.example.healthapp.screens.content.home.ScreenContent
import com.example.healthapp.screens.mainscreens.AuthScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavGraph(navController: NavHostController, userViewModel: UserViewModel, bpmDailyRepository: BpmDailyRepository, bpmHourlyRepository: BpmHourlyRepository, stepsHourlyRepository: StepsHourlyRepository, stepsDailyRepository: StepsDailyRepository) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeContent(navController = navController, userViewModel = userViewModel)
        }
        composable(route = BottomBarScreen.Sleep.route) {
            ScreenContent(
                name = BottomBarScreen.Sleep.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Workout.route) {
            ScreenContent(
                name = BottomBarScreen.Workout.route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.Profile.route) {

            ProfileContent(navController = navController, userViewModel = userViewModel)
        }
        composable(route = BottomBarScreen.Steps.route) {
            BpmContent(bpmHourlyRepository = bpmHourlyRepository, bpmDailyRepository = bpmDailyRepository, stepsDailyRepository = stepsDailyRepository, stepsHourlyRepository = stepsHourlyRepository)
        }
        composable(route = BottomBarScreen.ProfileSettings.route) {
            ProfileSettingsContent(navController = navController, userViewModel = userViewModel)
        }
        composable(route = "SETTINGS") {
            SettingsContent(navController = navController)
        }
        composable(route = Graph.AUTHENTICATION) {
            AuthScreen(userViewModel = userViewModel, startDestination = "LOGIN", bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsHourlyRepository = stepsHourlyRepository, stepsDailyRepository = stepsDailyRepository)
        }
    }
}