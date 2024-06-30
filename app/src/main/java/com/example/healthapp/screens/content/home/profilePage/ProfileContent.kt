package com.example.healthapp.screens.content.home.profilePage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.database.sleep.SleepDaily
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.users.User
import com.example.healthapp.database.users.UserViewModel
import com.example.healthapp.database.workouts.Workout
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.VeryLightGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileContent(navController: NavHostController, userViewModel: UserViewModel, sleepRepository: SleepDailyRepository, workoutRepository: WorkoutRepository) {
    val scope = rememberCoroutineScope()
    val colorOnPrimary = colors.onPrimary
    var sleepList7Days by remember { mutableStateOf<List<SleepDaily>>(emptyList()) }
    var workoutList7Days by remember { mutableStateOf<List<Workout>>(emptyList()) }
    val currentTime = LocalDateTime.now()
    val startOfNextDay8Pm = currentTime.plusDays(0).withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfWeek8Pm = currentTime.minusDays(6).withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfWeek = currentTime.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    var user by remember { mutableStateOf<User?>(null) }
    var bmiColor by remember { mutableStateOf(colorOnPrimary) }
    var activityColor by remember { mutableStateOf(colorOnPrimary) }
    var finalActivityValue by remember { mutableIntStateOf(0) }
    var averageActiveTime by remember { mutableStateOf("0h 0min") }
    var averageSleepTime by remember { mutableStateOf("0h 0min") }

    LaunchedEffect(Unit) {
        scope.launch {
            user = userViewModel.getUser()
            val bmi: Float = (user?.bmi ?: 0f).toFloat()
            bmiColor = when {
                bmi < 18.5 -> Color(0XFF56B4E3)
                bmi < 24.9 -> Color(0XFF20A072)
                bmi < 29.9 -> Color(0XFFEBCB65)
                bmi < 34.9 -> Color(0xFFE89323)
                else -> Color(0XFFC4311D)
            }
            sleepList7Days = withContext(Dispatchers.IO) {
                sleepRepository.getAllPast7days(startOfWeek8Pm, startOfNextDay8Pm)
            }
            workoutList7Days = withContext(Dispatchers.IO) {
                workoutRepository.getAllPast7days(startOfWeek, startOfNextDay)
            }

            // Calculate the total duration of workouts in milliseconds
            val totalWorkoutDurationMillis = workoutList7Days.sumOf { it.duration }
            // Convert milliseconds to hours and minutes
            val totalWorkoutDurationMinutes = totalWorkoutDurationMillis / (1000 * 60) / 7
            val hours = totalWorkoutDurationMinutes / 60
            val minutes = totalWorkoutDurationMinutes % 60
            averageActiveTime = "${hours}h ${minutes}min"

            val activityLevel: Int = user?.activityLevel ?: 0
            activityColor = when {
                totalWorkoutDurationMinutes < 30 -> Color(0xFFE89323)
                totalWorkoutDurationMinutes in 30..59 -> Color(0XFFEBCB65)
                totalWorkoutDurationMinutes in 60..89 -> Color(0XFF56B4E3)
                else -> Color(0XFF20A072)
            }
            finalActivityValue = when {
                totalWorkoutDurationMinutes < 30 -> 0
                totalWorkoutDurationMinutes in 30..59 -> 1
                totalWorkoutDurationMinutes in 60..89 -> 2
                else -> 3
            }

            // Calculate average sleep time in a similar way
            val totalSleepDurationMinutes = sleepList7Days.sumOf { it.REMDuration + it.lightDuration + it.deepDuration } / 7
            val sleepHours = totalSleepDurationMinutes / 60
            val sleepMinutes = totalSleepDurationMinutes % 60
            averageSleepTime = "${sleepHours}h ${sleepMinutes}min"

        }
    }

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(color = VeryLightGray)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                    .padding(top = 24.dp, start = 24.dp, end = 20.dp, bottom = 24.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    user?.let {
                        Text(
                            text = it.fullName,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 28.sp,  // Increased font size
                            fontWeight = FontWeight.Bold, // Bold text
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Age: ", fontSize = 19.sp)
                            Text(text = user?.age.toString(), fontSize = 21.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Height: ", fontSize = 19.sp)
                            Text(text = "${user?.height}cm", fontSize = 21.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Weight: ", fontSize = 19.sp)
                            Text(text = "${user?.weight}kg", fontSize = 21.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Gender: ", fontSize = 19.sp)
                            user?.gender?.let {
                                Text(text = it, fontSize = 21.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "BMI: ", fontSize = 19.sp)
                            Text(text = String.format("%.1f", user?.bmi), fontSize = 21.sp, color = bmiColor) // Formatted BMI
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Activity Level: ", fontSize = 19.sp)
                                val activityLevelValue = activityLevelMap[finalActivityValue] ?: 0
                                Text(
                                    text = "$finalActivityValue ($activityLevelValue)", // Display both the level and the value
                                    fontSize = 18.sp,
                                    color = activityColor
                                )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Align the button to the right
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End // Align to the right
                        ) {
                            Button(
                                onClick = { navController.navigate("PROFILE/SETTINGS") },
                                modifier = Modifier
                                    .width(120.dp) // Adjust width as needed
                                    .height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                            ) {
                                Text(
                                    "Edit",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                    .padding(top = 24.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Weekly Summary",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart), // Replace with your menu icon resource
                            tint = Color(0XFFD877C9),
                            contentDescription = "Heart Icon",
                            modifier = Modifier.size(32.dp) // Adjust the size as needed
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Average Active Time",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            averageActiveTime,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Average Sleep Time",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            averageSleepTime,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(85.dp))
        }
    }
}

val activityLevelMap: Map<Int, String> = mapOf(
    0 to "Sedentary",
    1 to "Lightly Active",
    2 to "Moderately Active",
    3 to "Very Active"
)
