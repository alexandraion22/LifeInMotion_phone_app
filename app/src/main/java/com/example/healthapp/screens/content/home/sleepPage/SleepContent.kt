package com.example.healthapp.screens.content.home.sleepPage

import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthapp.R
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.sleep.SleepDaily
import com.example.healthapp.database.sleep.SleepDailyRepository
import com.example.healthapp.database.users.User
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.VeryLightGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    }

    Column {
        Box(
            modifier = Modifier
                .background(color = VeryLightGray)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                        .verticalScroll(rememberScrollState())
                )
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                            .padding(top = 18.dp, start = 12.dp, end = 12.dp, bottom = 4.dp)
                            .clickable {
                                navController.navigate("SLEEP/INDIVIDUAL")
                            }
                    )
                    {
                        if(todaysSleep!=null)
                            SleepSummary(todaysSleep!!)
                        else
                        {
                            Column(modifier = Modifier
                                .padding(8.dp)) {
                                Text(text = "Tonight's sleep", fontSize = 24.sp)
                                Text("No sleep recorded", fontSize = 16.sp)
                            }
                        }
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
                        if(todaysSleep!=null)
                            SleepScoreCard(navController = navController)
                        else
                        {
                            Column(modifier = Modifier
                                .padding(8.dp)) {
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
                                Text("No sleep recorded", fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SleepGraphContent(sleepRepository = sleepRepository)
                    Spacer(modifier = Modifier.height(40.dp))
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
fun SleepSummaryIndividual(sleepEntry: SleepDaily) {
    Column(modifier = Modifier
        .padding(8.dp)) {
        Text(text = "Sleep summary",  fontSize = 24.sp)

        val totalSleep  = sleepEntry.lightDuration+sleepEntry.deepDuration+sleepEntry.REMDuration
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

            Spacer(modifier = Modifier.height(4.dp))
            Row{
                Text("Automatic score")
                Spacer(modifier = Modifier.weight(1f))
                Text(sleepEntry.automaticScore.toString())
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row{
                Text("Manual score")
                Spacer(modifier = Modifier.weight(1f))
                Text(sleepEntry.givenScore.toString())
            }
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
                Text(text = "${floor(value).toInt()} h")
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SleepGraphContent(
    sleepRepository: SleepDailyRepository
) {
    var sleepList7Days by remember { mutableStateOf<List<SleepDaily>>(emptyList()) }

    val colorOnPrimary = MaterialTheme.colors.onPrimary
    val currentTime = LocalDateTime.now()
    val startOfNextDay = currentTime.plusDays(0).withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfWeek = currentTime.minusDays(6).withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    LaunchedEffect(Unit) {
        sleepList7Days = withContext(Dispatchers.IO) {
            sleepRepository.getAllPast7days(startOfWeek,startOfNextDay)
        }
    }
        Column(
            modifier = Modifier
                .height(500.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                SleepBarChart7Days(sleepList7Days, colorOnPrimary = colorOnPrimary)
            }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SleepBarChart7Days(sleepList: List<SleepDaily>, colorOnPrimary: Color) {
    if (sleepList.isEmpty()) {
        Column {}
        return
    }
    val currentTime = LocalDateTime.now()
    val pastWeekDates = (0..6).map { currentTime.minusDays(it.toLong()).toLocalDate() }.reversed()
    val dailyData = pastWeekDates.associateWith { date ->
        sleepList.find { sleep -> LocalDateTime.ofInstant(Instant.ofEpochMilli(sleep.timestampStart), ZoneId.systemDefault()).toLocalDate() == date }
    }

    val chartHeight = 1125f // Adjust height as needed
    val hoursInDay = 12
    val factor = chartHeight / hoursInDay

    Column {
        Text(
            text = "Sleep consistency",
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val barWidth = 60f
            val barSpacing = 124f

            // Create Paint object for dotted lines
            val paint = Paint().asFrameworkPaint().apply {
                color = Color.Gray.toArgb()
                strokeWidth = 2f
                pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f) // Dotted effect
            }

            // Draw horizontal dotted lines for each hour of the day
            for (i in 0 until hoursInDay) {
                val yPos = i * factor
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawLine(
                        0f,
                        yPos,
                        825f,
                        yPos,
                        paint
                    )
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "${if (i == 0) 12 else i % 12} ${if (i < 12) "A.M." else "P.M."}",
                    size.width - 50f,
                    yPos,
                    Paint().asFrameworkPaint().apply {
                        color = colorOnPrimary.toArgb()
                        textSize = 24f // Adjust text size as needed
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                    }
                )
            }

            dailyData.entries.withIndex().forEach { (index, entry) ->
                val (date, sleep) = entry
                sleep?.let {
                    val totalSleepDuration = it.REMDuration + it.lightDuration + it.deepDuration
                    val startHour = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestampStart), ZoneId.systemDefault()).hour
                    val startMinute = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestampStart), ZoneId.systemDefault()).minute
                    val startYPos = (startHour + startMinute / 60f) * factor
                    val barHeight = totalSleepDuration * factor / 60f // convert minutes to hours

                    drawRoundRect(
                        color = PsychedelicPurple,
                        topLeft = Offset(barSpacing * index, startYPos),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }

                // Draw date labels
                drawIntoCanvas {
                    val formattedDate = date.format(DateTimeFormatter.ofPattern("E"))

                    it.nativeCanvas.drawText(
                        formattedDate,
                        barSpacing * index + barWidth / 2 - 25f,
                        chartHeight + 12f, // Position below the bars
                        Paint().asFrameworkPaint().apply {
                            color = colorOnPrimary.toArgb()
                            textSize = 36f // Adjust text size as needed
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                        }
                    )
                }
            }
        }
    }
}
