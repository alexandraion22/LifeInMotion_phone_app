package com.example.healthapp.screens.mainscreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    stepsHourlyRepository: StepsHourlyRepository,
    bpmRepository: BpmRepository,
    workoutScheduleRepository: WorkoutScheduleRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    activityDailyRepository: ActivityDailyRepository,
    goalsRepository: GoalsRepository,
    workoutRepository: WorkoutRepository,
    sleepDailyRepository: SleepDailyRepository
) {
    AuthNavGraph(navController = navController, userViewModel = userViewModel, startDestination = startDestination, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsDailyRepository = stepsDailyRepository, stepsHourlyRepository = stepsHourlyRepository, bpmRepository = bpmRepository, workoutScheduleRepository = workoutScheduleRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository, goalsRepository = goalsRepository, workoutRepository = workoutRepository, sleepDailyRepository = sleepDailyRepository)
}