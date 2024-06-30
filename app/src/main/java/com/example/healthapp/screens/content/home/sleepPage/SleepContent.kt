package com.example.healthapp.screens.content.home.sleepPage

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthapp.R
import com.example.healthapp.database.sleep.SleepDaily
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.users.User
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.math.floor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SleepContent(
    sleepRepository: SleepDailyRepository,
    navController: NavController
) {
    var allSleeps by remember { mutableStateOf<List<SleepDaily>>(emptyList()) }
    var todaysSleep by remember { mutableStateOf<SleepDaily?>(null) }

    LaunchedEffect(Unit) {
        val timeYesterday8Pm = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis() - 240000
        val timeToday8Pm = LocalDateTime.now().withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        allSleeps = withContext(Dispatchers.IO) {
            sleepRepository.getEntriesForDay(timeYesterday8Pm,timeToday8Pm)
        }
        if(allSleeps.isNotEmpty())
            todaysSleep = allSleeps[0]
        Log.e("HERE", allSleeps.toString())
    }

    Column {
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
                        .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                )
                {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                            .padding(top = 18.dp, start = 12.dp, end = 12.dp, bottom = 4.dp)
                    )
                    {
                        todaysSleep?.let { SleepSummary(it) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                            .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
                    )
                    {
                        SleepScoreCard(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun SleepSummary(sleepEntry: SleepDaily) {
    Column(modifier = Modifier
        .padding(8.dp)) {
        Text(text = "Tonight's sleep",  fontSize = 24.sp)

        val totalSleep  =sleepEntry.lightDuration+sleepEntry.deepDuration+sleepEntry.REMDuration
        Column (modifier = Modifier
            .padding(4.dp)){
            Spacer(modifier = Modifier.height(8.dp))
            val colorGreen = Color(0XFF20A072)
            val colorYellow = Color(0XFFEBCB65)
            val colorOrange = Color(0xFFE89323)
            val colorRed = Color(0XFFFF5733)

            HourBar(label = "Total Sleep Time", value = totalSleep/60f, valueMax = 7, color = colorGreen)
            NormalBar(label = "Sleep Cycles", value = sleepEntry.cycles.toFloat(), valueMax = 7, textVal = "cycles", color = colorOrange)
            NormalBar(label = "Awakenings", value = sleepEntry.awakenings.toFloat(), valueMax = 4, textVal = "awakenings", color = colorOrange)

            Bar(label = "Deep Sleep", percentage = sleepEntry.deepDuration/(totalSleep.toFloat()) * 100, color = colorGreen)
            Bar(label = "Light Sleep", percentage = sleepEntry.lightDuration/(totalSleep.toFloat()) * 100, color = colorGreen)
            Bar(label = "REM Sleep", percentage = sleepEntry.REMDuration/(totalSleep.toFloat()) *100, color = colorGreen)
        }
    }
}

@Composable
fun HourBar(label: String, value: Float, valueMax: Int, color: Color) {
    Column {
        Row{
            Text(text = label)
            Spacer(modifier = Modifier.weight(1f))
            val fractionalPart = value - floor(value)
            if(fractionalPart!=0f)
                Text(text = "${floor(value).toInt()} h ${(fractionalPart * 60).toInt()} min")
            else
                Text(text = "${floor(value)} h")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(color = color.copy(alpha = 0f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        (value / valueMax)
                            .coerceAtMost(1f)
                    )
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun NormalBar(label: String, value: Float, valueMax: Int, textVal:String, color: Color) {
    Column {
        Row{
            Text(text = label)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${value.toInt()} $textVal")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(color = color.copy(alpha = 0f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        (value / valueMax)
                            .coerceAtMost(1f)
                    )
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun Bar(label: String, percentage: Float, color: Color) {
    Column {
        Row{
            Text(text = label)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${percentage.toInt()}%")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(color = color.copy(alpha = 0f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun SleepScoreCard(navController: NavController) {

        val colorGreen = Color(0XFF20A072)
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sleep score",
                    fontSize = 24.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_navbar_sleep), // Replace with actual icon resource
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column (modifier = Modifier.padding(horizontal = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "95",
                        color = colorGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Excellent",
                        color = colorGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Take a moment to rate your sleep in order to get better insights"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("SLEEP/RATE") },
                    colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple)
                ) {
                    Text(
                        "Rate Sleep",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }
}