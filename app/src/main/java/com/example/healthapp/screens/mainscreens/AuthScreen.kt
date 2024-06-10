package com.example.healthapp.screens.mainscreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.graphs.AuthNavGraph

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AuthScreen(
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    startDestination: String,
    bpmDailyRepository: BpmDailyRepository,
    bpmHourlyRepository: BpmHourlyRepository,
    stepsDailyRepository: StepsDailyRepository,
    stepsHourlyRepository: StepsHourlyRepository
) {
    AuthNavGraph(navController = navController, userViewModel = userViewModel, startDestination = startDestination, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsDailyRepository = stepsDailyRepository, stepsHourlyRepository = stepsHourlyRepository)
}