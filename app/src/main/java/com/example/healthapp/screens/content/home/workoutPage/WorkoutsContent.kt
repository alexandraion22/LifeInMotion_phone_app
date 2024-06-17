package com.example.healthapp.screens.content.home.workoutPage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.healthapp.R
import com.example.healthapp.database.schedule.WorkoutSchedule
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import com.example.healthapp.database.workouts.Workout
import com.example.healthapp.database.workouts.WorkoutRepository
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.math.floor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutsContent(workoutScheduleRepository: WorkoutScheduleRepository, navController: NavController, workoutRepository: WorkoutRepository) {
    var isWorkoutScheduleExpanded by remember { mutableStateOf(true) }
    val textSize = 16.sp

    var dayIndex by remember { mutableIntStateOf(0) }
    val workoutSchedules = remember { List(8) { mutableStateOf<Set<String>>(emptySet()) } }
    var workoutsDoneSet by remember { mutableStateOf<Set<String>>(emptySet()) }
    var workoutsDone by remember { mutableStateOf<List<Workout>>(emptyList()) }

    var workoutDurationTotal by remember { mutableIntStateOf(0) }
    var workoutCaloriesTotal by remember { mutableIntStateOf(0) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    if (dayIndex == 0) {
        LaunchedEffect(Unit) {
            workoutsDone = withContext(Dispatchers.IO) {
                workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
            }
            workoutsDoneSet = workoutsDone.map { it.type }.toSet()
            workoutDurationTotal = ((workoutsDone.sumOf { it.duration })/60000).toInt()
            workoutCaloriesTotal = workoutsDone.sumOf { it.calories }
            for (day in 1..7) {
                    workoutSchedules[day].value = withContext(Dispatchers.IO) {
                        workoutScheduleRepository.getListForDay(day)?.workouts ?: emptySet()
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxHeight(0.94f).background(color = VeryLightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.98f)
                    .padding(horizontal = 10.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Spacer (modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.25f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(PsychedelicPurple)
                        .clickable { navController.navigate("WORKOUT/LIST") }
                        .padding(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row {
                            Text(
                                "Today's Workouts",
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        }
                        Row {
                            Text(
                                "Total workout duration",
                                color = Color.White,
                                fontSize = textSize
                            )
                            Spacer(modifier = Modifier.weight(1f)) // Add space between columns
                            Text(
                                text = formatWorkoutDuration(workoutDurationTotal),                                color = Color.White,
                                fontSize = textSize
                            )
                        }
                        Row {
                            Text(
                                "Total calories burned",
                                color = Color.White,
                                fontSize = textSize
                            )
                            Spacer(modifier = Modifier.weight(1f)) // Add space between columns

                            Text(
                                workoutCaloriesTotal.toString() + "kcal",
                                color = Color.White,
                                fontSize = textSize
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            if(workoutsDone.isNotEmpty()) {
                                Text(
                                    "Workout types",
                                    color = Color.White,
                                    fontSize = textSize
                                )
                                Spacer(modifier = Modifier.weight(1f)) // Add space between columns
                                Column(horizontalAlignment = Alignment.End) {
                                    val workoutTypeDisplayName = mapOf(
                                        "circuit_training" to "Circuit Training",
                                        "aerobic" to "Aerobic",
                                        "pilates" to "Pilates",
                                        "weights" to "Weight Lifting",
                                        "walk" to "Walking",
                                        "run" to "Running"
                                    )
                                    workoutsDoneSet.forEach { workoutType ->
                                        Text(
                                            text = workoutTypeDisplayName[workoutType]
                                                ?: workoutType, // Default to workoutType if no mapping is found
                                            color = Color.White,
                                            fontSize = textSize
                                        )
                                    }
                                }
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
                        .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isWorkoutScheduleExpanded = !isWorkoutScheduleExpanded
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(6.dp))
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
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val daysIndexes = mapOf(
                                    "Monday" to 1,
                                    "Tuesday" to 2,
                                    "Wednesday" to 3,
                                    "Thursday" to 4,
                                    "Friday" to 5,
                                    "Saturday" to 6,
                                    "Sunday" to 7
                                )

                                // Maintain a mutable map to track expanded states of each day
                                val expandedStates =
                                    remember { mutableStateMapOf<String, Boolean>() }

                                daysIndexes.forEach { (day,_) ->
                                    val isExpanded = expandedStates[day] ?: true
                                    DayItem(day, isExpanded, workoutScheduleRepository, workoutSchedules
                                        , { expandedStates[day] = !isExpanded }) {
                                        dayIndex = daysIndexes[day]!!
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
    else
    {
        YoutubeContent(dayIndex = dayIndex, workoutScheduleRepository = workoutScheduleRepository)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DayItem(day: String, isExpanded: Boolean, workoutScheduleRepository: WorkoutScheduleRepository, workoutsSchedules: List<MutableState<Set<String>>>, onClick: () -> Unit, onAddVideoClick: () -> Unit) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val workoutsSchedulesLocal = remember { List(8) { mutableStateOf<Set<String>>(emptySet()) } }
    val daysIndexes = mapOf(
        "Monday" to 1,
        "Tuesday" to 2,
        "Wednesday" to 3,
        "Thursday" to 4,
        "Friday" to 5,
        "Saturday" to 6,
        "Sunday" to 7
    )
    var videoIdToDelete by remember { mutableStateOf("") }

    coroutineScope.launch {
        for (dayIndex in 1..7) {
            workoutsSchedulesLocal[dayIndex].value = withContext(Dispatchers.IO) {
                workoutScheduleRepository.getListForDay(dayIndex)?.workouts ?: emptySet()
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this video from the workout list?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            daysIndexes[day]?.let { workoutScheduleRepository.deleteVideoFromDay(it, videoIdToDelete) }
                            videoIdToDelete = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, KindaLightGray, RoundedCornerShape(16.dp))
            .background(VeryLightGray),
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
                    modifier = Modifier.background(VeryLightGray),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Row of images
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        val videoIds = workoutsSchedulesLocal[daysIndexes[day]!!].value.toList()
                        videoIds.take(10).forEach { videoId ->
                            Box{
                                Image(
                                    painter = rememberImagePainter(
                                        "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
                                    ),
                                    contentDescription = "Thumbnail for $day",
                                    modifier = Modifier
                                        .size(width = 150.dp, height = 90.dp)
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .clickable {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://www.youtube.com/watch?v=${videoId}")
                                            )
                                            context.startActivity(intent)
                                        },
                                    contentScale = ContentScale.Crop
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_minus),
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(4.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .background(color = Color.White)
                                        .clickable {
                                            showDialog = true
                                            videoIdToDelete = videoId
                                        })
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        Button(onClick = onAddVideoClick,
                                colors = ButtonDefaults.buttonColors(backgroundColor = PsychedelicPurple))
                        {

                            Text(text = "Add", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun formatWorkoutDuration(durationInMinutes: Int): String {
    return when {
        durationInMinutes < 60 -> "${durationInMinutes}min"
        else -> {
            val hours = floor(durationInMinutes.toDouble() / 60).toInt()
            val minutes = durationInMinutes % 60
            "${hours}h ${minutes}min"
        }
    }
}