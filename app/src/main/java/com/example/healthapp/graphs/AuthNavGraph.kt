package com.example.healthapp.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthapp.database.activity.ActivityDailyRepository
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.calories.CaloriesDailyRepository
import com.example.healthapp.database.goals.GoalsRepository
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.screens.content.auth.LoginContent
import com.example.healthapp.screens.content.auth.SignUpContent
import com.example.healthapp.screens.content.auth.SignUpDetailsContent
import com.example.healthapp.screens.mainscreens.HomeScreen


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AuthNavGraph(navController: NavHostController, userViewModel: UserViewModel, startDestination: String, bpmDailyRepository: BpmDailyRepository, bpmHourlyRepository: BpmHourlyRepository, stepsHourlyRepository: StepsHourlyRepository, stepsDailyRepository: StepsDailyRepository, bpmRepository: BpmRepository, workoutScheduleRepository: WorkoutScheduleRepository, caloriesDailyRepository: CaloriesDailyRepository, activityDailyRepository: ActivityDailyRepository, goalsRepository: GoalsRepository, workoutRepository: WorkoutRepository, sleepDailyRepository: SleepDailyRepository) {
    NavHost(
        navController = navController,
        route = Graph.AUTHENTICATION,
        startDestination = startDestination
    ) {
        composable(route = "LOGIN") {
            LoginContent(
                navController = navController,
                userViewModel = userViewModel
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
            HomeScreen(userViewModel = userViewModel, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsHourlyRepository = stepsHourlyRepository, stepsDailyRepository = stepsDailyRepository, bpmRepository = bpmRepository, workoutScheduleRepository = workoutScheduleRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository, goalsRepository = goalsRepository, workoutRepository = workoutRepository, sleepDailyRepository = sleepDailyRepository)
        }
    }
}