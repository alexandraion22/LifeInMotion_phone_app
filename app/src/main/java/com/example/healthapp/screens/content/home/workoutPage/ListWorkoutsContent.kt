package com.example.healthapp.screens.content.home.workoutPage

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.activity.ActivityDaily
import com.example.healthapp.database.activity.ActivityDailyRepository
import com.example.healthapp.database.calories.CaloriesDaily
import com.example.healthapp.database.calories.CaloriesDailyRepository
import com.example.healthapp.database.workouts.Workout
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.service.toEpochMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun ListWorkoutsContent(
    workoutRepository: WorkoutRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    activityDailyRepository: ActivityDailyRepository
) {
    var dayWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    LaunchedEffect(currentTime) {
        dayWorkouts = withContext(Dispatchers.IO) {
            workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
        }
        Log.e("TAG", dayWorkouts.toString())
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { currentTime = currentTime.minusDays(1) }) {
                Text(text = "Back")
            }
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                style = MaterialTheme.typography.h6
            )
            Button(
                onClick = { currentTime = currentTime.plusDays(1) },
                enabled = currentTime.toLocalDate() != LocalDateTime.now().toLocalDate()
            ) {
                Text(text = "Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dayWorkouts.isEmpty()) {
            Text(
                text = "No workouts registered",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            dayWorkouts.forEach { workout ->
                WorkoutCard(
                    workout = workout,
                    workoutRepository = workoutRepository,
                    activityDailyRepository = activityDailyRepository,
                    caloriesDailyRepository = caloriesDailyRepository,
                    startOfDay = startOfDay
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    workoutRepository: WorkoutRepository,
    activityDailyRepository: ActivityDailyRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    startOfDay: Long
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Type: ${workout.type}")
            Text(text = "Duration: ${workout.duration / 60000} minute/s")
            Text(text = "Calories: ${workout.calories} kcal")
            Text(text = "Min HR: ${workout.minHR}")
            Text(text = "Max HR: ${workout.maxHR}")
            Text(text = "Average HR: ${workout.meanHR}")

            Spacer(modifier = Modifier.height(8.dp))

            if (workout.autoRecorder && !workout.confirmed) {
                Button(
                    onClick = {
                        // TODO: Implement edit functionality
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = "Edit")
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            workoutRepository.deleteEntryById(workout.id)
                            val activityDaily = activityDailyRepository.getEntryForDay(startOfDay)
                            val caloriesDaily = caloriesDailyRepository.getEntryForDay(startOfDay)
                            Log.e("TAG",caloriesDaily.toString())
                            if (activityDaily != null) {
                                activityDailyRepository.update(ActivityDaily(timestamp = startOfDay, activeTime = activityDaily.activeTime - (workout.duration / 60000).toInt()))
                            }
                            if (caloriesDaily != null) {
                                caloriesDailyRepository.update(CaloriesDaily(timestamp = startOfDay, totalCalories = caloriesDaily.totalCalories - workout.calories))
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = "Delete")
            }
        }
    }
}
