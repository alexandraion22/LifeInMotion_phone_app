package com.example.healthapp.screens.content.home.sleepPage

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.sleep.SleepDaily
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.workouts.Workout
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SuppressLint("NewApi")
@Composable
fun IndividualSleepContent(
    sleepDailyRepository: SleepDailyRepository
) {

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    var allSleeps by remember { mutableStateOf<List<SleepDaily>>(emptyList()) }
    var todaysSleep by remember { mutableStateOf<SleepDaily?>(null) }

    LaunchedEffect(currentTime) {
        val timeYesterday8Pm = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis() - 240000
        val timeToday8Pm = currentTime.withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        allSleeps = withContext(Dispatchers.IO) {
            sleepDailyRepository.getEntriesForDay(timeYesterday8Pm,timeToday8Pm)
        }
        if(allSleeps.isNotEmpty())
            todaysSleep = allSleeps[0]
        else
            todaysSleep = null
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

        if (todaysSleep==null) {
            Text(
                text = "No sleep recorded",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Column( modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .verticalScroll(rememberScrollState()))
            {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(color = Color.White)
                        .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                        .padding(top = 18.dp, start = 12.dp, end = 12.dp, bottom = 4.dp)
                )
                {
                    SleepSummaryIndividual(sleepEntry = todaysSleep!!)
                }
            }
        }
    }
}