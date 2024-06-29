package com.example.healthapp.screens.content.home.sleepPage

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
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray

@Composable
fun SleepContent(
    sleepRepository: SleepDailyRepository,
    navController: NavController
) {
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
                        .padding(start = 12.dp, top = 16.dp, end = 12.dp)
                )
                {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                            .padding(top = 24.dp, start = 24.dp, end = 20.dp, bottom = 24.dp)
                    )
                    {
                        SleepSummary(sleepData = null)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                            .padding(top = 24.dp, start = 24.dp, end = 20.dp, bottom = 24.dp)
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
fun SleepSummary(sleepData: SleepDaily?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Tonight's sleep", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Total Sleep Time: 4")
        Text(text = "Sleep Cycles: 3")

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Awakenings: 2")

        Spacer(modifier = Modifier.height(8.dp))

        Bar(label = "Deep Sleep", percentage = 20, color = Color(0xFFFFA500))
        Bar(label = "Light Sleep", percentage = 30, color = Color(0xFF00FF00))
        Bar(label = "REM Sleep", percentage = 30, color = Color(0xFF0000FF))
    }
}

@Composable
fun Bar(label: String, percentage: Int, color: Color) {
    Column {
        Text(text = "$label ${percentage}%")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(color = color.copy(alpha = 0f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight(0.7f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun SleepScoreCard(navController: NavController) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sleep score",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_navbar_sleep), // Replace with actual icon resource
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "95",
                    style = MaterialTheme.typography.h4.copy(
                        color = Color(0xFF00C853), // Green color
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Excellent",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color(0xFF00C853), // Green color
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Excellent",
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("SLEEP/RATE") },
                colors = ButtonDefaults.buttonColors(containerColor = PsychedelicPurple),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Rate sleep")
            }
        }
}