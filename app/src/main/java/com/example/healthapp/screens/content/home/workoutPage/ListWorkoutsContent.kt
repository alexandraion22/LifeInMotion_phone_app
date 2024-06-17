package com.example.healthapp.screens.content.home.workoutPage

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.healthapp.R
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import okhttp3.internal.wait
import java.time.Instant
import java.time.ZoneId

@SuppressLint("NewApi")
@Composable
fun ListWorkoutsContent(
    workoutRepository: WorkoutRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    activityDailyRepository: ActivityDailyRepository
) {
    var dayWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    val coroutineScope = rememberCoroutineScope()

    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    LaunchedEffect(currentTime, dayWorkouts) {
        dayWorkouts = withContext(Dispatchers.IO) {
            workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple),
                onClick = { currentTime = currentTime.minusDays(1) }) {
                Text(text = "Back")
            }
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple),
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
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Column( modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .verticalScroll(rememberScrollState()))
            {
            dayWorkouts.forEach { workout ->
                WorkoutCard(
                    workout = workout,
                    workoutRepository = workoutRepository,
                    activityDailyRepository = activityDailyRepository,
                    caloriesDailyRepository = caloriesDailyRepository,
                    startOfDay = startOfDay,
                    onDelete = {
                        coroutineScope.launch {
                        dayWorkouts = workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutCard(
    workout: Workout,
    workoutRepository: WorkoutRepository,
    activityDailyRepository: ActivityDailyRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    startOfDay: Long,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val workoutTypeIconMap = mapOf(
        "aerobic" to R.drawable.ic_aerobic,
        "walk" to R.drawable.ic_walk,
        "run" to R.drawable.ic_run,
        "circuit_training" to R.drawable.ic_circuit_training,
        "weights" to R.drawable.ic_weightlifting,
        "pilates" to R.drawable.ic_pilates
    )
    val workoutTypeDisplayName = mapOf(
        "circuit_training" to "Circuit Training",
        "aerobic" to "Aerobic",
        "pilates" to "Pilates",
        "weights" to "Weight Lifting",
        "walk" to "Walking",
        "run" to "Running"
    )
    val workoutStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(workout.timestamp), ZoneId.systemDefault())
    val workoutEndTime = workoutStartTime.plusMinutes(workout.duration/60000)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTimeFormatted = workoutStartTime.format(timeFormatter)
    val endTimeFormatted = workoutEndTime.format(timeFormatter)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color = Color.White)
            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp)),
        elevation = 4.dp
    ) {
        Column{
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Spacer(modifier = Modifier.width(74.dp))
                Text(
                    text = workoutTypeDisplayName[workout.type] ?: workout.type,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                // Icon with painter resource (replace with actual painter resource)
                Icon(
                    painter = painterResource(id = workoutTypeIconMap[workout.type] ?: R.drawable.ic_walk),
                    contentDescription = "Workout Icon",
                    modifier = Modifier.size(48.dp),
                    tint = PsychedelicPurple
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Column with max, min, and mean HR
                Column {
                    Text(text = "Max HR: ${workout.maxHR}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Min HR: ${workout.minHR}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Mean HR: ${workout.meanHR}")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Column with calories and duration
                Column {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(painter = painterResource(id = R.drawable.ic_burn), contentDescription = "Burn",
                            modifier = Modifier.size(28.dp),
                            tint = PsychedelicPurple)
                        Text(text = " ${workout.calories} kcal", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$startTimeFormatted - $endTimeFormatted", fontSize = 16.sp)
                }

                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            onClick = {
                                // TODO: Implement edit functionality
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .border(1.dp, KindaLightGray, RoundedCornerShape(24.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            onClick = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        workoutRepository.deleteEntryById(workout.id)
                                        val activityDaily =
                                            activityDailyRepository.getEntryForDay(startOfDay)
                                        val caloriesDaily =
                                            caloriesDailyRepository.getEntryForDay(startOfDay)
                                        if (activityDaily != null) {
                                            activityDailyRepository.update(
                                                ActivityDaily(
                                                    timestamp = startOfDay,
                                                    activeTime = activityDaily.activeTime - (workout.duration / 60000).toInt()
                                                )
                                            )
                                        }
                                        if (caloriesDaily != null) {
                                            caloriesDailyRepository.update(
                                                CaloriesDaily(
                                                    timestamp = startOfDay,
                                                    totalCalories = caloriesDaily.totalCalories - workout.calories
                                                )
                                            )
                                        }
                                        onDelete()
                                    }
                                }
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, KindaLightGray, RoundedCornerShape(24.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                }
            }
        }
    }
}

@Composable
fun formatDuration(durationInMillis: Long): String {
    val minutes = durationInMillis / 60000
    return if (minutes < 60) {
        "$minutes min"
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        "$hours h $remainingMinutes min"
    }
}
