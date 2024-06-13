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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.database.goals.Goals
import com.example.healthapp.database.goals.GoalsRepository
import com.example.healthapp.screens.content.auth.CustomTextField
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.customTextFieldColors
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SetGoalsContent(goalsRepository: GoalsRepository) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var stepsGoal by remember { mutableStateOf("") }
    var caloriesGoal by remember { mutableStateOf("") }
    var activityGoal by remember { mutableStateOf("") }
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    val scaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Set Your Goals",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = stepsGoal,
                onValueChange = { stepsGoal = it },
                label = "Steps Goal",
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { /* move focus to the next field */ }),
                keyboardType = KeyboardType.Number,
                customTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = caloriesGoal,
                onValueChange = { caloriesGoal = it },
                label = "Calories Goal",
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { /* move focus to the next field */ }),
                keyboardType = KeyboardType.Number,
                customTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = activityGoal,
                onValueChange = { activityGoal = it },
                label = "Activity Goal (minutes)",
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                keyboardType = KeyboardType.Number,
                textFieldColors = customTextFieldColors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        val steps = stepsGoal.toIntOrNull()
                        val calories = caloriesGoal.toIntOrNull()
                        val activity = activityGoal.toIntOrNull()
                        if (steps != null && calories != null && activity != null) {
                            val goals = Goals(
                                timestamp = startOfDay,
                                stepsGoal = steps,
                                caloriesGoal = calories,
                                activityGoal = activity
                            )
                            scope.launch {
                                Log.e("HERE1","YES")
                                goalsRepository.deleteAllGoals()
                                goalsRepository.insert(goals)
                                scaffoldState.snackbarHostState.showSnackbar("Goals saved successfully")
                            }
                        } else {
                            scope.launch {
                                Log.e("HERE1","NO")
                                scaffoldState.snackbarHostState.showSnackbar("Please enter valid numbers for all fields")
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
                        // Handle cancel action
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
