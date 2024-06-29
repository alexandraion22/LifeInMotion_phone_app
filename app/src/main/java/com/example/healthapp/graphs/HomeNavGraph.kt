package com.example.healthapp.graphs

import SettingsContent
import BpmContent
import SetGoalsContent
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
import com.example.healthapp.screens.mainscreens.BottomBarScreen
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.screens.content.home.mainPage.HomeContent
import com.example.healthapp.screens.content.home.profilePage.ProfileContent
import com.example.healthapp.screens.content.home.profilePage.ProfileSettingsContent
import com.example.healthapp.screens.content.home.ScreenContent
import com.example.healthapp.screens.content.home.workoutPage.WorkoutsContent
import com.example.healthapp.screens.content.home.mainPage.StepsContent
import com.example.healthapp.screens.content.home.sleepPage.RateSleepContent
import com.example.healthapp.screens.content.home.sleepPage.SleepContent
import com.example.healthapp.screens.content.home.workoutPage.AddWorkoutContent
import com.example.healthapp.screens.content.home.workoutPage.ListWorkoutsContent
import com.example.healthapp.screens.mainscreens.AuthScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavGraph(navController: NavHostController, userViewModel: UserViewModel, bpmDailyRepository: BpmDailyRepository, bpmHourlyRepository: BpmHourlyRepository, stepsHourlyRepository: StepsHourlyRepository, stepsDailyRepository: StepsDailyRepository, bpmRepository: BpmRepository, workoutScheduleRepository: WorkoutScheduleRepository, caloriesDailyRepository: CaloriesDailyRepository, activityDailyRepository: ActivityDailyRepository, goalsRepository: GoalsRepository, workoutRepository: WorkoutRepository, sleepDailyRepository: SleepDailyRepository) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeContent(navController = navController, userViewModel = userViewModel, stepsDailyRepository = stepsDailyRepository, bpmRepository = bpmRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository, goalsRepository = goalsRepository)
        }

        composable(route = BottomBarScreen.Workout.route) {
            WorkoutsContent(workoutScheduleRepository = workoutScheduleRepository, navController = navController, workoutRepository = workoutRepository)
        }

        composable(route = BottomBarScreen.WorkoutList.route) {
            ListWorkoutsContent(workoutRepository = workoutRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository)
        }

        composable(route = BottomBarScreen.Profile.route) {
            ProfileContent(navController = navController, userViewModel = userViewModel)
        }

        composable(route = BottomBarScreen.Sleep.route) {
            SleepContent(sleepRepository = sleepDailyRepository, navController = navController)
        }

        composable(route = BottomBarScreen.RateSleep.route) {
            RateSleepContent(sleepRepository = sleepDailyRepository, navController = navController)
        }


        composable(route = BottomBarScreen.Bpm.route) {
            BpmContent(bpmHourlyRepository = bpmHourlyRepository, bpmDailyRepository = bpmDailyRepository)
        }

        composable(route = BottomBarScreen.Steps.route) {
            StepsContent(stepsHourlyRepository = stepsHourlyRepository, stepsDailyRepository = stepsDailyRepository)
        }

        composable(route = BottomBarScreen.SetGoals.route) {
            SetGoalsContent(goalsRepository = goalsRepository, navController = navController)
        }

        composable(route = BottomBarScreen.ProfileSettings.route) {
            ProfileSettingsContent(navController = navController, userViewModel = userViewModel)
        }

        composable(route = "SETTINGS") {
            SettingsContent(navController = navController)
        }

        composable(route = Graph.AUTHENTICATION) {
            AuthScreen(userViewModel = userViewModel, startDestination = "LOGIN", bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsHourlyRepository = stepsHourlyRepository, stepsDailyRepository = stepsDailyRepository, bpmRepository = bpmRepository, workoutScheduleRepository = workoutScheduleRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository, goalsRepository = goalsRepository, workoutRepository = workoutRepository, sleepDailyRepository = sleepDailyRepository)
        }
    }
}