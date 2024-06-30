package com.example.healthapp.screens.content.home.sleepPage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material.Text
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.users.User
import com.example.healthapp.screens.content.home.profilePage.getValidationMessage
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import com.example.healthapp.utils.calculateBMI
import kotlinx.coroutines.launch

@Composable
fun RateSleepContent(
    sleepRepository: SleepDailyRepository,
    navController: NavController
) {
    // State to hold the current slider value
    var sliderValue by remember { mutableFloatStateOf(100f) }
    Box(
        modifier = Modifier
            .background(color = VeryLightGray)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 22.dp, top = 32.dp, end = 22.dp)
            )
            {
                Spacer(modifier = Modifier.height(54.dp))
                Text(
                    text = "Sleep rating",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 12.dp)
                )
                Text(
                    text = "Rate your last night's sleep, on a scale from 1 to 100",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 16.dp, start = 12.dp)
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(color = Color.White)
                        .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                        .padding(top = 24.dp, start = 24.dp, end = 20.dp, bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        // Display the current value
                        Text(text = "Current rating: ${sliderValue.toInt()}")
                        // Slider from 1 to 100
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 1f..100f,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            colors = SliderDefaults.colors(
                                thumbColor = PsychedelicPurple,
                                activeTrackColor = PsychedelicPurple
                            )
                        )
                        Row (modifier = Modifier.fillMaxWidth(0.95f)){
                            Text(text = "1")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "100")
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row( Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)) {
                            Button(
                                onClick = {
                                        navController.navigate("SLEEP")
                                },
                                modifier = Modifier
                                    .width(132.dp)
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                            ) {
                                Text(
                                    "Save",
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.2f))
                            Button(
                                onClick = {
                                    navController.navigate("SLEEP")
                                },
                                modifier = Modifier
                                    .width(132.dp)
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
                            ) {
                                Text(
                                    "Cancel",
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(800.dp))
            }
        }
    }
}
