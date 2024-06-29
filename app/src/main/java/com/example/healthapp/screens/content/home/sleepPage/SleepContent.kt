package com.example.healthapp.screens.content.home.sleepPage

import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.healthapp.database.sleep.SleepDailyRepository

@Composable
fun SleepContent(
    sleepRepository: SleepDailyRepository,
    navController: NavController
) {
    Button(onClick = { navController.navigate("SLEEP/RATE")}) {
    }
}