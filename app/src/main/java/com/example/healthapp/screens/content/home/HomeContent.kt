package com.example.healthapp.screens.content.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.healthapp.database.users.UserViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthapp.R
import com.example.healthapp.database.bpm.last.Bpm
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.steps.daily.StepsDaily
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.users.User
import com.example.healthapp.service.toEpochMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import com.example.healthapp.ui.theme.DarkPurple
import com.example.healthapp.ui.theme.LightPurple
import com.example.healthapp.ui.theme.PsychedelicPurple

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(navController: NavHostController, userViewModel: UserViewModel, stepsDailyRepository: StepsDailyRepository, bpmRepository: BpmRepository) {
    val scope = rememberCoroutineScope()
    var fullName by remember { mutableStateOf("") }
    var stepsToday by remember { mutableStateOf<StepsDaily?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    var stepsTodayK by remember { mutableDoubleStateOf(0.0) }
    var bpmLast by remember { mutableStateOf<Bpm?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            user = userViewModel.getUser()
            fullName = user?.fullName ?: "User"
        }
        stepsToday = withContext(Dispatchers.IO) {
            stepsDailyRepository.getEntryForDay(startOfDay)
        }
        stepsTodayK = String.format("%.1f", (stepsToday?.steps ?: 0) / 1000.0).toDouble()
        bpmLast = withContext(Dispatchers.IO) {
            bpmRepository.getFirst()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column( horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                // First Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // Half the width of the screen
                        .fillMaxHeight(0.18f)
                        .padding(10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/BPM") }
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Pulse",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${bpmLast?.bpm}",
                                    fontSize = 28.sp, // Adjusted font size for the number
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                                Text(
                                    text = "BPM",
                                    fontSize = 14.sp, // Adjusted font size for "BPM"
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }
                        }
                        // Align the icon to the top
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heartrate),
                            contentDescription = "heartrate",
                            tint = colors.onPrimary,
                            modifier = Modifier
                                .align(Alignment.Top)
                        )
                    }
                }

                // Second Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Half the width of the screen
                        .fillMaxHeight(0.18f)
                        .padding(10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/Steps") }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Activities",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${stepsTodayK}K",
                                    fontSize = 28.sp, // Adjusted font size for the number
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                                Text(
                                    text = "steps",
                                    fontSize = 14.sp, // Adjusted font size for "BPM"
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }
                        }
                        // Align the icon to the top
                        Icon(
                            painter = painterResource(id = R.drawable.ic_steps),
                            contentDescription = "steps",
                            tint = colors.onPrimary,
                            modifier = Modifier
                                .align(Alignment.Top)
                        )
                    }
                }
            }

            Row {
                // First Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // Half the width of the screen
                        .fillMaxHeight(0.22f)
                        .padding(10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/BPM") }
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Sleep",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Text(
                                text = "score",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "95",
                                    fontSize = 28.sp, // Adjusted font size for the number
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                                Text(
                                    text = "/100",
                                    fontSize = 14.sp, // Adjusted font size for "BPM"
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }
                        }
                        // Align the icon to the top
                        Icon(
                            painter = painterResource(id = R.drawable.ic_navbar_sleep),
                            contentDescription = "heartrate",
                            tint = colors.onPrimary,
                            modifier = Modifier
                                .align(Alignment.Top)
                        )
                    }
                }

                // Second Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Half the width of the screen
                        .fillMaxHeight(0.22f)
                        .padding(10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/Steps") }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Burned",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Text(
                                text = "calories",
                                fontSize = 16.sp,
                                color = colors.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "327",
                                    fontSize = 28.sp, // Adjusted font size for the number
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                                Text(
                                    text = "kcal",
                                    fontSize = 14.sp, // Adjusted font size for "BPM"
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }
                        }
                        // Align the icon to the top
                        Icon(
                            painter = painterResource(id = R.drawable.ic_burn),
                            contentDescription = "kcal",
                            tint = colors.onPrimary,
                            modifier = Modifier
                                .align(Alignment.Top)
                                .size(44.dp)
                        )
                    }
                }
            }
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Half the width of the screen
                        .fillMaxHeight(0.6f)
                        .padding(top = 10.dp, end = 10.dp, bottom = 15.dp, start = 10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/Steps") }
                        .padding(top = 15.dp, start = 10.dp, bottom = 0.dp, end = 15.dp),
                    contentAlignment = Alignment.Center) {
                    Row {
                        Column {
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = "Today's goals",
                                fontSize = 24.sp
                            )
                            CircularProgress(
                                stepsProgress = 0.9f,
                                caloriesBurnedProgress = 0.75f,
                                workoutProgress = 0.8f,
                                size = 200.dp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f)) // Add space between columns
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Spacer(modifier = Modifier.height(52.dp))
                            Text(
                                text = "Steps",
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(bottom = 4.dp),
                                text = "1500/2100",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Calories burned",
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(bottom = 4.dp),
                                text = "327/350",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Active time",
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(bottom = 4.dp),
                                text = "103/150 minutes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Half the width of the screen
                        .fillMaxHeight(0.7f)
                        .padding(top = 5.dp , bottom = 20.dp, start = 10.dp, end = 10.dp) // Add space between boxes
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .clickable { navController.navigate("HOME/Steps") }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center,
                )
                {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Row {
                            Text(
                                text = "Streak",
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            text = "Well done",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "You’ve kept your healthy streak for 2 days",
                            fontSize = 14.sp

                        )
                    }

                }
            }
        }
    }
}

@Composable
fun CircularProgress(caloriesBurnedProgress: Float, stepsProgress: Float, workoutProgress: Float, size: Dp) {
    val strokeWidth = 22f
    val radius = (500 - 2 * strokeWidth) / 2 // Adjusted for stroke width
    val sweepAngle1 = stepsProgress * 360
    val sweepAngle2 = caloriesBurnedProgress * 360
    val sweepAngle3 = workoutProgress * 360
    val iconPainter: Painter = painterResource(id = R.drawable.ic_heart)
    Canvas(modifier = Modifier
        .size(size)
        .padding(16.dp)) {

        translate(left = -30f, top = -10f) {
            // Draw the background circle
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth, strokeWidth),
                size = Size(radius * 2, radius * 2)
            )

            // Draw the outer progress arc
            drawArc(
                color = PsychedelicPurple,
                startAngle = -90f,
                sweepAngle = sweepAngle1,
                useCenter = false,
                style = Stroke(width = strokeWidth * 1.25f, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth, strokeWidth),
                size = Size(radius * 2, radius * 2)
            )

            // Calculate the inner dimensions
            val innerRadius = radius * 0.8f

            // Draw the first inner circle
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    strokeWidth + (radius - innerRadius),
                    strokeWidth + (radius - innerRadius)
                ),
                size = Size(innerRadius * 2, innerRadius * 2)
            )

            // Draw the first inner progress arc
            drawArc(
                color = DarkPurple,
                startAngle = -90f,
                sweepAngle = sweepAngle2,
                useCenter = false,
                style = Stroke(width = strokeWidth * 1.25f, cap = StrokeCap.Round),
                topLeft = Offset(
                    strokeWidth + (radius - innerRadius),
                    strokeWidth + (radius - innerRadius)
                ),
                size = Size(innerRadius * 2, innerRadius * 2)
            )

            // Calculate the dimensions for the second inner circle
            val innerRadius2 = innerRadius * 0.75f

            // Draw the second inner circle
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    strokeWidth + (radius - innerRadius2),
                    strokeWidth + (radius - innerRadius2)
                ),
                size = Size(innerRadius2 * 2, innerRadius2 * 2)
            )

            // Draw the second inner progress arc
            drawArc(
                color = LightPurple,
                startAngle = -90f,
                sweepAngle = sweepAngle3,
                useCenter = false,
                style = Stroke(width = strokeWidth * 1.25f, cap = StrokeCap.Round),
                topLeft = Offset(
                    strokeWidth + (radius - innerRadius2),
                    strokeWidth + (radius - innerRadius2)
                ),
                size = Size(innerRadius2 * 2, innerRadius2 * 2)
            )

            translate(left = 195f, top = 195f) {
                with(iconPainter) {
                    draw(
                        iconPainter.intrinsicSize,
                        colorFilter = ColorFilter.tint(PsychedelicPurple)
                    )
                }
            }
        }
    }
}
