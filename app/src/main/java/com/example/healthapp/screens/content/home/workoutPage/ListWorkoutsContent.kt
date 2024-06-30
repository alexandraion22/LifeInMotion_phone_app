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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.Card
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import com.example.healthapp.screens.content.auth.CustomSnackbarHost
import com.example.healthapp.screens.content.auth.CustomTextField
import com.example.healthapp.screens.content.auth.DropdownList
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import com.example.healthapp.ui.theme.customTextFieldColors
import kotlinx.coroutines.CoroutineScope
import java.time.Instant
import java.time.ZoneId

@SuppressLint("NewApi")
@Composable
fun ListWorkoutsContent(
    workoutRepository: WorkoutRepository,
    caloriesDailyRepository: CaloriesDailyRepository,
    activityDailyRepository: ActivityDailyRepository
) {

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    var isAdding by remember { mutableStateOf(false) }
    var isEditingId by remember { mutableIntStateOf(-1) }
    if(isAdding)
    {
        AddWorkoutContent(
            workoutRepository = workoutRepository,
            activityDailyRepository = activityDailyRepository,
            caloriesDailyRepository = caloriesDailyRepository,
            currentTime = currentTime
        ) { isAdding = false }
        return
    }
    Log.e("HERE",isEditingId.toString())
    if(isEditingId!=-1){
        EditWorkoutContent(
            workoutRepository =  workoutRepository,
            editingId = isEditingId
        ) {
            isEditingId = -1
        }
        return
    }

    var dayWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    LaunchedEffect(currentTime, dayWorkouts, isAdding) {
        dayWorkouts = withContext(Dispatchers.IO) {
            workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
        }
        Log.e("TAG",dayWorkouts.toString())
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = VeryLightGray)
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
            Column {
                Text(
                    text = "No workouts registered",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Ensure items are aligned to the end
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple),
                        onClick = { isAdding = true }) {
                        Text(text = "Add Workout")
                    }
                }
            }
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
                                dayWorkouts =
                                    workoutRepository.getEntriesForDay(startOfDay, startOfNextDay)
                            }
                        }
                    ) {
                        isEditingId = workout.id
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row( modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Ensure items are aligned to the end
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple),
                        onClick = { isAdding = true}) {
                        Text(text = "Add Workout")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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
    onDelete: () -> Unit,
    function: () -> Unit
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

                Column {
                    Text(
                        text = workoutTypeDisplayName[workout.type] ?: workout.type,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = when {
                                isMidnight(workout.timestamp) -> "Manually Added"
                                workout.autoRecorder -> "Auto Recorded"
                                else -> "Recorded on Watch"
                            },
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
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

                Spacer(modifier = Modifier.weight(1f))
                // Column with calories and duration
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(painter = painterResource(id = R.drawable.ic_burn), contentDescription = "Burn",
                            modifier = Modifier.size(28.dp),
                            tint = PsychedelicPurple)
                        Text(text = " ${workout.calories} kcal", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text =  when {
                        isMidnight(workout.timestamp) -> ((workout.duration/60000).toString() + " min")
                        else -> "$startTimeFormatted - $endTimeFormatted" },
                        fontSize = 16.sp)
                }

                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF20A072)),
                            onClick = {
                                    function()
                            },
                            enabled = workout.autoRecorder
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(0.dp) // Ensure no padding is added to the icon
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
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
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isMidnight(timestamp: Long): Boolean {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    return dateTime.hour == 0 && dateTime.minute == 0 && dateTime.second == 0 && dateTime.nano == 0
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddWorkoutContent(workoutRepository: WorkoutRepository, activityDailyRepository: ActivityDailyRepository, caloriesDailyRepository: CaloriesDailyRepository, currentTime: LocalDateTime, func : ()-> Unit) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var minBpm by remember { mutableStateOf("") }
    var meanBpm by remember { mutableStateOf("") }
    var maxBpm by remember { mutableStateOf("") }
    val workoutTypes = listOf("circuit_training", "pilates","weights","aerobic","run","walk")
    val workoutNames = listOf("Circuit Training", "Pilates","Weightlifting","Aerobic","Run","Walking")
    var duration by remember { mutableStateOf("") }
    var burnedCalories  by remember { mutableStateOf("") }
    val meanBpmFocusRequester = remember { FocusRequester() }
    val maxBpmFocusRequester = remember { FocusRequester() }
    val durationFocusRequester = remember { FocusRequester() }
    val burnedCaloriesFocusRequester = remember { FocusRequester() }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val startOfDay =currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0)

    androidx.compose.material.Scaffold(scaffoldState = scaffoldState,
        snackbarHost = {
            CustomSnackbarHost(it, Modifier.padding(bottom = 80.dp))
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(VeryLightGray)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                androidx.compose.material.Text(
                    text = "Add a new workout",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(start = 14.dp, bottom = 12.dp),
                    textAlign = TextAlign.Left
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                Column {
                    CustomTextField(
                        value = minBpm,
                        onValueChange = { minBpm = it },
                        label = "Min BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { maxBpmFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = maxBpm,
                        onValueChange = { maxBpm = it },
                        label = "Max BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { meanBpmFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = maxBpmFocusRequester,
                        textFieldColors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = meanBpm,
                        onValueChange = { meanBpm = it },
                        label = "Mean BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { durationFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = meanBpmFocusRequester,
                        textFieldColors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = "Duration (minutes)",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { burnedCaloriesFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = durationFocusRequester,
                        textFieldColors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = burnedCalories,
                        onValueChange = { burnedCalories = it },
                        label = "Burned Calories",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = burnedCaloriesFocusRequester,
                        textFieldColors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownList(
                        itemList = workoutNames,
                        selectedIndex = selectedIndex,
                        onItemClick = { selectedIndex = it },
                        width = 0.835f
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        androidx.compose.material.Button(
                            onClick = {
                                val resultString = checkValues(minBpm,maxBpm,meanBpm,duration,burnedCalories)
                                if (resultString.isEmpty()) {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            val newWorkout = Workout (
                                                minHR = minBpm.toInt(),
                                                maxHR = maxBpm.toInt(),
                                                meanHR = meanBpm.toInt(),
                                                calories = burnedCalories.toInt(),
                                                timestamp = startOfDay.toEpochMillis(),
                                                duration = (duration.toInt() * 60000).toLong(),
                                                confirmed = true,
                                                autoRecorder = false,
                                                type = workoutTypes[selectedIndex]
                                            )
                                            workoutRepository.insert(newWorkout)
                                            activityDailyRepository.deleteEntryForDay(startOfDay.toEpochMillis())
                                            caloriesDailyRepository.deleteEntryForDay(startOfDay.toEpochMillis())
                                            val activityDaily =
                                                activityDailyRepository.getEntryForDay(startOfDay.toEpochMillis())
                                            val caloriesDaily =
                                                caloriesDailyRepository.getEntryForDay(startOfDay.toEpochMillis())
                                            val activeBefore = activityDaily?.activeTime ?: 0
                                            val caloriesBefore = caloriesDaily?.totalCalories ?: 0
                                            activityDailyRepository.update(
                                                ActivityDaily(
                                                    timestamp = startOfDay.toEpochMillis(),
                                                    activeTime = (newWorkout.duration / 60000).toInt()
                                                        .coerceAtMost(30) + activeBefore
                                                )
                                            )
                                            caloriesDailyRepository.update(
                                                CaloriesDaily(
                                                    timestamp = startOfDay.toEpochMillis(),
                                                    totalCalories = newWorkout.calories + caloriesBefore
                                                )
                                            )
                                        }
                                    }
                                    func()
                                } else {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(resultString)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material.MaterialTheme.colors.secondary
                            )
                        ) {
                            androidx.compose.material.Text("Save", color = Color.White)
                        }

                        androidx.compose.material.Button(
                            onClick = {
                                func()
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material.MaterialTheme.colors.primaryVariant
                            )
                        ) {
                            androidx.compose.material.Text("Cancel", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun EditWorkoutContent(workoutRepository: WorkoutRepository, editingId : Int, func : ()-> Unit) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var minBpm by remember { mutableStateOf("") }
    var meanBpm by remember { mutableStateOf("") }
    var maxBpm by remember { mutableStateOf("") }
    val workoutTypes = listOf("circuit_training", "pilates","weights","aerobic","run","walk")
    val workoutNames = listOf("Circuit Training", "Pilates","Weightlifting","Aerobic","Run","Walking")
    var duration by remember { mutableStateOf("") }
    var burnedCalories  by remember { mutableStateOf("") }
    val meanBpmFocusRequester = remember { FocusRequester() }
    val maxBpmFocusRequester = remember { FocusRequester() }
    val durationFocusRequester = remember { FocusRequester() }
    val burnedCaloriesFocusRequester = remember { FocusRequester() }
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    var workout by remember { mutableStateOf<Workout?>(null) }

    scope.launch {
        workout = withContext(Dispatchers.IO) {
            workoutRepository.getEntryById(editingId)
        }

        minBpm = (workout?.minHR ?:0).toString()
        maxBpm = (workout?.maxHR ?:0).toString()
        meanBpm = (workout?.meanHR ?:0).toString()
        duration = ((workout?.duration ?:0)/60000).toInt().toString()
        burnedCalories = (workout?.calories ?:0).toString()
        selectedIndex = when (workout?.type) {
            "circuit_training" -> 0
            "pilates" -> 1
            "weights" -> 2
            "aerobic" -> 3
            "run" -> 4
            "walk" -> 5
            else -> 0
        }
    }

    androidx.compose.material.Scaffold(scaffoldState = scaffoldState,
        snackbarHost = {
            CustomSnackbarHost(it, Modifier.padding(bottom = 80.dp))
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(VeryLightGray)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                androidx.compose.material.Text(
                    text = "Edit workout",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(start = 14.dp, bottom = 12.dp),
                    textAlign = TextAlign.Left
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                Column {
                    CustomTextField(
                        value = minBpm,
                        onValueChange = { minBpm = it },
                        label = "Min BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { maxBpmFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        customTextFieldColors(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = maxBpm,
                        onValueChange = { maxBpm = it },
                        label = "Max BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { meanBpmFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = maxBpmFocusRequester,
                        textFieldColors = customTextFieldColors(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = meanBpm,
                        onValueChange = { meanBpm = it },
                        label = "Mean BPM",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { durationFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = meanBpmFocusRequester,
                        textFieldColors = customTextFieldColors(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = "Duration (minutes)",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { burnedCaloriesFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = durationFocusRequester,
                        textFieldColors = customTextFieldColors(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = burnedCalories,
                        onValueChange = { burnedCalories = it },
                        label = "Burned Calories",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
                        keyboardType = KeyboardType.Number,
                        focusRequester = burnedCaloriesFocusRequester,
                        textFieldColors = customTextFieldColors(),
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownList(
                        itemList = workoutNames,
                        selectedIndex = selectedIndex,
                        onItemClick = { selectedIndex = it },
                        width = 0.835f
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        androidx.compose.material.Button(
                            onClick = {
                                val resultString = checkValues(minBpm,maxBpm,meanBpm,duration,burnedCalories)
                                if (resultString.isEmpty()) {
                                    val newWorkout = workout?.let { it1 ->
                                        Workout (
                                            minHR = it1.minHR,
                                            maxHR = it1.maxHR,
                                            meanHR = it1.meanHR,
                                            calories = it1.calories,
                                            timestamp = it1.timestamp,
                                            duration = it1.duration,
                                            confirmed = it1.confirmed,
                                            autoRecorder = it1.autoRecorder,
                                            type = workoutTypes[selectedIndex]
                                        )
                                    }
                                    scope.launch {
                                        if (newWorkout != null) {
                                            workoutRepository.update(id = editingId, newWorkout)
                                        }
                                    }
                                    func()
                                } else {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(resultString)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material.MaterialTheme.colors.secondary
                            )
                        ) {
                            androidx.compose.material.Text("Save", color = Color.White)
                        }

                        androidx.compose.material.Button(
                            onClick = {
                                func()
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material.MaterialTheme.colors.primaryVariant
                            )
                        ) {
                            androidx.compose.material.Text("Cancel", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

fun checkValues(minBpmString: String, maxBpmString: String, meanBpmString: String, durationString: String, caloriesBurnedString: String) : String {

    try {
        val minBpm = minBpmString.toInt()
        if (minBpm <=0 ) {
            return "The minimum BPM must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The minimum BPM must have a valid value."
    }
    try {
        val maxBpm = maxBpmString.toInt()
        if (maxBpm <=0 ) {
            return "The maximum BPM must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The maximum BPM must have a valid value."
    }
    try {
        val meanBpm = meanBpmString.toInt()
        if (meanBpm <=0 ) {
            return "The mean BPM must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The mean BPM must have a valid value."
    }
    try {
        val duration = durationString.toInt()
        if (duration <=0 ) {
            return "The duration must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The duration must have a valid value."
    }
    try {
        val caloriesBurned = caloriesBurnedString.toInt()
        if (caloriesBurned <=0 ) {
            return "The calories burned must have a valid value."
        }
    } catch (e: NumberFormatException)  {
        return "The calories burned must have a valid value."
    }
    return ""
}