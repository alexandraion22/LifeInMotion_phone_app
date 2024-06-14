import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthapp.database.goals.Goals
import com.example.healthapp.database.goals.GoalsRepository
import com.example.healthapp.screens.content.auth.CustomSnackbarHost
import com.example.healthapp.screens.content.auth.CustomTextField
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.VeryLightGray
import com.example.healthapp.ui.theme.customTextFieldColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SetGoalsContent(goalsRepository: GoalsRepository, navController: NavController) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var stepsGoal by remember { mutableStateOf("") }
    var caloriesGoal by remember { mutableStateOf("") }
    var activityGoal by remember { mutableStateOf("") }
    val caloriesFocusRequester = remember { FocusRequester() }
    val activityFocusRequester = remember { FocusRequester() }
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    var goals by remember { mutableStateOf<Goals?>(null) }
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        scope.launch {
            goals = withContext(Dispatchers.IO) {
                goalsRepository.getFirst()
            }

            stepsGoal = (goals?.stepsGoal ?: 0).toString()
            caloriesGoal = (goals?.caloriesGoal ?: 0).toString()
            activityGoal = (goals?.activityGoal ?: 0).toString()
        }
    }
    Scaffold( scaffoldState = scaffoldState,
        snackbarHost = { CustomSnackbarHost(it, Modifier.padding(bottom = 240.dp))
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(VeryLightGray)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row (
                modifier = Modifier.fillMaxWidth(0.9f)
            ){
                Text(
                    text = "Set Your Goals",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(start = 14.dp, bottom = 12.dp),
                    textAlign = TextAlign.Left
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            ){
                Column {
                    CustomTextField(
                        value = stepsGoal,
                        onValueChange = { stepsGoal = it },
                        label = "Steps Goal",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { caloriesFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = caloriesGoal,
                        onValueChange = { caloriesGoal = it },
                        label = "Burned Calories Goal (kcal)",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = { activityFocusRequester.requestFocus() }),
                        keyboardType = KeyboardType.Number,
                        textFieldColors = customTextFieldColors(),
                        focusRequester = caloriesFocusRequester
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = activityGoal,
                        onValueChange = { activityGoal = it },
                        label = "Activity Goal (minutes)",
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        keyboardType = KeyboardType.Number,
                        textFieldColors = customTextFieldColors(),
                        focusRequester = activityFocusRequester
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                val resultString = checkNumbers(stepsGoal,caloriesGoal,activityGoal)
                                if(resultString.isEmpty()){
                                    val steps = stepsGoal.toInt()
                                    val calories = caloriesGoal.toInt()
                                    val activity = activityGoal.toInt()
                                    val goals = Goals(
                                        timestamp = startOfDay,
                                        stepsGoal = steps,
                                        caloriesGoal = calories,
                                        activityGoal = activity
                                    )
                                    scope.launch {
                                        goalsRepository.deleteAllGoals()
                                        goalsRepository.insert(goals)
                                        navController.navigate("HOME")
                                    }
                                } else {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(resultString)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                        ) {
                            Text("Save", color = Color.White)
                        }

                        Button(
                            onClick = {
                                navController.navigate("HOME")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
                        ) {
                            Text("Cancel", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

fun checkNumbers(stepsString: String, caloriesString: String, activityString: String) : String {

    try {
        val steps = stepsString.toInt()
        if (steps <=0 ) {
            return "The steps must have a valid value."
        }
        if (steps % 100!=0) {
            return "Steps must be a multiple of 100."
        }
    } catch (e: NumberFormatException)  {
        return "The steps must have a valid value."
    }
    try {
        val calories = caloriesString.toInt()
        if(calories <=0)
            return "The calories must have a valid value."
        if (calories %10!=0) {
            return "The calories must be a multiple of 10."
        }
    } catch (e: NumberFormatException)  {
        return "The calories must have a valid value."
    }
    try {
        val activity = activityString.toInt()
        if (activity <= 0) {
            return "The  activity time must have a valid value."
        }
        if (activity % 5!= 0) {
            return "The  activity time must be a multiple of 5."
        }
    } catch (e: NumberFormatException)  {
        return "The weight must have a valid value."
    }
    return ""
}