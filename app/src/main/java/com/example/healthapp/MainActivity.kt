package com.example.healthapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.healthapp.database.users.UserViewModelFactory
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.graphs.Graph
import com.example.healthapp.graphs.RootNavigationGraph
import com.example.healthapp.ui.theme.HealthAppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bpmHourlyRepository: BpmHourlyRepository

    @Inject
    lateinit var bpmDailyRepository: BpmDailyRepository

    @Inject
    lateinit var stepsHourlyRepository: StepsHourlyRepository

    @Inject
    lateinit var stepsDailyRepository: StepsDailyRepository

    @Inject
    lateinit var bpmRepository: BpmRepository

    @Inject
    lateinit var workoutScheduleRepository: WorkoutScheduleRepository

    @Inject
    lateinit var caloriesDailyRepository: CaloriesDailyRepository

    @Inject
    lateinit var activityDailyRepository: ActivityDailyRepository

    @Inject
    lateinit var goalsRepository: GoalsRepository

    @Inject
    lateinit var workoutRepository: WorkoutRepository

    @Inject
    lateinit var sleepDailyRepository: SleepDailyRepository

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthAppTheme {
                val startRoute = remember { mutableStateOf(Graph.WELCOME) }
                val startDestination = remember { mutableStateOf("LOGIN") }
                val scope = rememberCoroutineScope()
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(application)
                )
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val user = userViewModel.getUser()
                        val uid = user?.uid
                        if(currentUser==null)
                            startRoute.value = Graph.AUTHENTICATION
                        else
                        {
                            if(currentUser.uid == uid)
                                startRoute.value = Graph.HOME
                            else
                            {
                                startRoute.value = Graph.AUTHENTICATION
                                startDestination.value = "SIGNUP/DETAILS"
                            }
                        }
                    }
                }
                RootNavigationGraph(navController = rememberNavController(), startRoute = startRoute.value, userViewModel = userViewModel, startDestinationPage = startDestination.value, bpmDailyRepository = bpmDailyRepository, bpmHourlyRepository = bpmHourlyRepository, stepsDailyRepository = stepsDailyRepository, stepsHourlyRepository = stepsHourlyRepository, bpmRepository = bpmRepository, workoutScheduleRepository = workoutScheduleRepository, caloriesDailyRepository = caloriesDailyRepository, activityDailyRepository = activityDailyRepository, goalsRepository = goalsRepository, workoutRepository = workoutRepository, sleepDailyRepository = sleepDailyRepository)
            }
        }
    }
}
