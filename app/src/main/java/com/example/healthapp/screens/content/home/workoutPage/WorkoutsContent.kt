package com.example.healthapp.screens.content.home.workoutPage

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.R
import com.example.healthapp.database.schedule.WorkoutSchedule
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import com.example.healthapp.ui.theme.PsychedelicPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WorkoutsContent(workoutScheduleRepository: WorkoutScheduleRepository) {
    var isWorkoutScheduleExpanded by remember { mutableStateOf(true) }
    val textSize = 16.sp

    var workoutScheduleMonday by remember { mutableStateOf<WorkoutSchedule?>(null) }

    LaunchedEffect(Unit) {
        workoutScheduleMonday = withContext(Dispatchers.IO) {
            workoutScheduleRepository.getListForDay(1)
        }
        Log.e("HERE",workoutScheduleMonday.toString())
    }
    Box(
        modifier = Modifier.fillMaxHeight(0.94f)
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(
                rememberScrollState()
            )) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PsychedelicPurple)
                    .padding(16.dp),
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    Row{
                        Text("Today's Workouts",
                            color = Color.White,
                            fontSize = 24.sp)
                    }
                    Row {
                        Text("Total workout duration",
                            color = Color.White,
                            fontSize = textSize)
                        Spacer(modifier = Modifier.weight(1f)) // Add space between columns
                        Text("2h 12min",
                            color = Color.White,
                            fontSize = textSize)
                    }
                    Row {
                        Text("Total calories burned",
                            color = Color.White,
                            fontSize = textSize)
                        Spacer(modifier = Modifier.weight(1f)) // Add space between columns

                        Text("437kcal",
                            color = Color.White,
                            fontSize = textSize)
                    }
                    Row (
                        verticalAlignment = Alignment.Top
                    ){
                        Text("Workout types",
                            color = Color.White,
                            fontSize = textSize)
                        Spacer(modifier = Modifier.weight(1f)) // Add space between columns
                        Column (horizontalAlignment = Alignment.End){
                            Text("Pilates",
                                color = Color.White,
                                fontSize = textSize)
                            Text("Circuit training",
                                color = Color.White,
                                fontSize = textSize)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp)),
                ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWorkoutScheduleExpanded = !isWorkoutScheduleExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Workout Schedule",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (isWorkoutScheduleExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isWorkoutScheduleExpanded) "Collapse" else "Expand"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(
                        visible = isWorkoutScheduleExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val days = listOf(
                                "Monday" to R.drawable.ic_heart, // Replace with actual drawable resources
                                "Tuesday" to R.drawable.ic_heart,
                                "Wednesday" to R.drawable.ic_heart,
                                "Thursday" to R.drawable.ic_heart,
                                "Friday" to R.drawable.ic_heart,
                                "Saturday" to R.drawable.ic_heart,
                                "Sunday" to R.drawable.ic_heart
                            )

                            // Maintain a mutable map to track expanded states of each day
                            val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

                            days.forEach { (day, image) ->
                                val isExpanded = expandedStates[day] ?: true
                                DayItem(day, image, isExpanded, workoutScheduleRepository) {
                                    expandedStates[day] = !isExpanded
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DayItem(day: String, image: Int, isExpanded: Boolean, workoutScheduleRepository: WorkoutScheduleRepository, onClick: () -> Unit,) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE2E8F0)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = day,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                ) {
                Column(
                    modifier = Modifier.background(Color(0xFFE2E8F0)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Row of images
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        val images = List(10) { image } // Replace with actual images if needed
                        images.take(10).forEach {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = "$day Image",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(2.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Button(onClick = {
                            coroutineScope.launch {
                                workoutScheduleRepository.addVideoToDay(1,"abcdef")
                            }
                        }) {
                            Text(text = "+")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
