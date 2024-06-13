package com.example.healthapp.screens.content.home.mainPage

import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.database.steps.daily.StepsDaily
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourly
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsContent(
    stepsHourlyRepository: StepsHourlyRepository,
    stepsDailyRepository: StepsDailyRepository
) {
    var stepsListHourly by remember { mutableStateOf<List<StepsHourly>>(emptyList()) }
    var stepsList7Days by remember { mutableStateOf<List<StepsDaily>>(emptyList()) }
    var stepsList31Days by remember { mutableStateOf<List<StepsDaily>>(emptyList()) }
    var selectedButton by remember { mutableIntStateOf(1) } // Initial selection is the first button

    val colorOnPrimary = colors.onPrimary
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfWeek = currentTime.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfMonth = currentTime.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()

    LaunchedEffect(Unit) {
        stepsListHourly = withContext(Dispatchers.IO) {
            stepsHourlyRepository.getAllPastDay(startOfDay, startOfNextDay)
        }
        stepsList7Days = withContext(Dispatchers.IO) {
            stepsDailyRepository.getAllPast7days(startOfWeek, startOfNextDay)
        }
        stepsList31Days = withContext(Dispatchers.IO) {
            stepsDailyRepository.getAllPast31days(startOfMonth, startOfNextDay)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 2.dp, bottom = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Steps",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .border(1.dp, KindaLightGray, RoundedCornerShape(24.dp))
                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .fillMaxHeight(0.075f)
                    .clip(RoundedCornerShape(50.dp))
                    .border(1.5.dp, Color.LightGray, RoundedCornerShape(40.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    FilledTonalButton(
                        modifier = Modifier
                            .width(111.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 1) colors.primary else Color.LightGray),
                        contentPadding = PaddingValues(0.dp), // Disabling the padding of the button
                        onClick = { selectedButton = 1 }
                    ) {
                        Text("Today",
                            color = colors.onPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .width(111.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 2) colors.primary else Color.LightGray),
                        contentPadding = PaddingValues(0.dp), // Disabling the padding of the button
                        onClick = { selectedButton = 2 }
                    ) {
                        Text("Past week",
                            color = colors.onPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .width(111.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 3) colors.primary else Color.LightGray),
                        contentPadding = PaddingValues(0.dp), // Disabling the padding of the button
                        onClick = { selectedButton = 3 }
                    ) {
                        Text("Past month",
                            color = colors.onPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                when (selectedButton) {
                    1 -> StepsHourly(stepsListHourly, colorOnPrimary = colorOnPrimary)
                    2 -> Steps7Days(stepsList7Days, colorOnPrimary = colorOnPrimary)
                    3 -> Steps31Days(stepsList31Days, colorOnPrimary = colorOnPrimary)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsHourly(stepsList: List<StepsHourly>, colorOnPrimary: Color) {
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val hourlyData = (0..23).associateWith { stepsList.find { steps -> (steps.timestamp - startOfDay).toInt() == it * 3600000 } }

    val maxSteps = stepsList.maxOfOrNull { it.steps } ?: 0
    val totalSteps = stepsList.sumOf { it.steps }


    // Handle the case where stepsList might be empty
    if (stepsList.isEmpty()) {
        Column {}
        return
    }

    // Round maxSteps to the next 10,000 multiplier
    val roundedMaxSteps = ceil(maxSteps / 5000.0).toInt() * 5000
    val stepInterval = roundedMaxSteps / 4
    val levels = (0..roundedMaxSteps step stepInterval).toList()

    Column {
        Text(
            text = "$totalSteps steps",
            modifier = Modifier.padding(top = 16.dp, start = 8.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorOnPrimary
        )
        Text(
            text = "(total)",
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            fontSize = 18.sp,
            color = colorOnPrimary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val barWidth = 20F
            val barSpacing = 35F
            val start =1100
            val heightFactor = (start - 500) * 1.5f / roundedMaxSteps.toFloat()

            // Draw horizontal dotted lines
            val paint = Paint().asFrameworkPaint().apply {
                color = Color.Gray.toArgb()
                strokeWidth = 2f
                pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
            }

            levels.forEach { steps ->
                val yPos = start - steps * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawLine(
                        0f,
                        yPos,
                        size.width - 50f,
                        yPos,
                        paint
                    )
                }
            }

            // Draw bars and legend
            hourlyData.forEach { (hour, steps) ->
                steps?.let {
                    val barHeight = it.steps * heightFactor
                    drawRoundRect(
                        color = PsychedelicPurple,
                        topLeft = Offset(barSpacing * hour, start - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }

                // Draw hour labels
                if (hour % 6 == 0 || hour == 23)
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            if (hour == 23) "(h)" else "$hour",
                            barSpacing * hour,
                            (start + 125).toFloat(), // Position below the bars
                            Paint().asFrameworkPaint().apply {
                                color = colorOnPrimary.toArgb()
                                textSize = 36f // Adjust text size as needed
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                            }
                        )
                    }
            }

            // Draw Y-axis labels on the right side
            levels.forEach { i ->
                val yPos = start - i * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        i.toString(),
                        size.width +50f,
                        yPos,
                        Paint().asFrameworkPaint().apply {
                            color = colorOnPrimary.toArgb()
                            textSize = 36f // Adjust text size as needed
                            textAlign = android.graphics.Paint.Align.RIGHT
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Steps7Days(stepsList: List<StepsDaily>, colorOnPrimary: Color) {
    val currentTime = LocalDateTime.now()
    val pastWeekDates = (0..6).map { currentTime.minusDays(it.toLong()).toLocalDate() }.reversed()
    val dailyData = pastWeekDates.associateWith { date ->
        stepsList.find { steps -> LocalDateTime.ofInstant(Instant.ofEpochMilli(steps.timestamp), ZoneId.systemDefault()).toLocalDate() == date }
    }

    val maxSteps = stepsList.maxOfOrNull { it.steps } ?: 0
    val totalSteps = stepsList.sumOf { it.steps }
    val averageSteps = totalSteps/7

    // Handle the case where stepsList might be empty
    if (stepsList.isEmpty()) {
        Column {}
        return
    }

    // Round maxSteps to the next 10,000 multiplier
    val roundedMaxSteps = ceil(maxSteps / 5000.0).toInt() * 5000
    val stepInterval = roundedMaxSteps / 4
    val levels = (0..roundedMaxSteps step stepInterval).toList()

    Column {
        Text(
            text = "$averageSteps steps/day",
            modifier = Modifier.padding(top = 16.dp, start = 8.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorOnPrimary
        )
        Text(
            text = "(average)",
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            fontSize = 18.sp,
            color = colorOnPrimary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val barWidth = 60F
            val barSpacing = 124F
            val start = 1100
            val heightFactor = (start - 500) * 1.5f / roundedMaxSteps.toFloat()

            // Draw horizontal dotted lines
            val paint = Paint().asFrameworkPaint().apply {
                color = Color.Gray.toArgb()
                strokeWidth = 2f
                pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
            }

            levels.forEach { steps ->
                val yPos = start - steps * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawLine(
                        0f,
                        yPos,
                        size.width - 50f,
                        yPos,
                        paint
                    )
                }
            }

            dailyData.entries.withIndex().forEach { (index, entry) ->
                val (date, steps) = entry
                steps?.let {
                    val barHeight = it.steps * heightFactor
                    drawRoundRect(
                        color = PsychedelicPurple,
                        topLeft = Offset(barSpacing * index, start - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }

                // Draw date labels
                drawIntoCanvas {
                    val formattedDate = if (date.dayOfMonth == 1) {
                        date.format(DateTimeFormatter.ofPattern("d/M"))
                    } else {
                        date.format(DateTimeFormatter.ofPattern("d"))
                    }

                    it.nativeCanvas.drawText(
                        formattedDate,
                        barSpacing * index + 28f,
                        (start + 125).toFloat(), // Position below the bars
                        Paint().asFrameworkPaint().apply {
                            color = colorOnPrimary.toArgb()
                            textSize = 36f // Adjust text size as needed
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                        }
                    )
                }
            }

            // Draw Y-axis labels on the right side
            levels.forEach { i ->
                val yPos = start - i * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        i.toString(),
                        size.width + 50f,
                        yPos,
                        Paint().asFrameworkPaint().apply {
                            color = colorOnPrimary.toArgb()
                            textSize = 36f // Adjust text size as needed
                            textAlign = android.graphics.Paint.Align.RIGHT
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Steps31Days(stepsList: List<StepsDaily>, colorOnPrimary: Color) {
    val currentTime = LocalDateTime.now()
    val pastMonthDates = (0..30).map { currentTime.minusDays(it.toLong()).toLocalDate() }.reversed()
    val dailyData = pastMonthDates.associateWith { date ->
        stepsList.find { steps -> LocalDateTime.ofInstant(Instant.ofEpochMilli(steps.timestamp), ZoneId.systemDefault()).toLocalDate() == date }
    }

    val maxSteps = stepsList.maxOfOrNull { it.steps } ?: 0
    val totalSteps = stepsList.sumOf { it.steps }
    val averageSteps = totalSteps/31

    // Handle the case where stepsList might be empty
    if (stepsList.isEmpty()) {
        Column {}
        return
    }

    // Round maxSteps to the next 10,000 multiplier
    val roundedMaxSteps = ceil(maxSteps / 5000.0).toInt() * 5000
    val stepInterval = roundedMaxSteps / 4
    val levels = (0..roundedMaxSteps step stepInterval).toList()

    Column {
        Text(
            text = "$averageSteps steps/day",
            modifier = Modifier.padding(top = 16.dp, start = 8.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorOnPrimary
        )
        Text(
            text = "(average)",
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            fontSize = 18.sp,
            color = colorOnPrimary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val barWidth = 13F // Adjusted bar width for 31 days
            val barSpacing = 26.5F // Adjusted bar spacing for 31 days
            val start = 1100
            val heightFactor = (start - 500) * 1.5f / roundedMaxSteps.toFloat()

            // Draw horizontal dotted lines
            val paint = Paint().asFrameworkPaint().apply {
                color = Color.Gray.toArgb()
                strokeWidth = 2f
                pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
            }

            levels.forEach { steps ->
                val yPos = start - steps * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawLine(
                        0f,
                        yPos,
                        size.width - 50f,
                        yPos,
                        paint
                    )
                }
            }

            dailyData.entries.withIndex().forEach { (index, entry) ->
                val (date, steps) = entry
                steps?.let {
                    val barHeight = it.steps * heightFactor
                    drawRoundRect(
                        color = PsychedelicPurple,
                        topLeft = Offset(barSpacing * index, start - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }

                // Draw date labels for the first entry, every 5th entry, and the first of the month
                if (index == 0 || index % 5 == 0 || date.dayOfMonth == 1) {
                    drawIntoCanvas {
                        val formattedDate = if (date.dayOfMonth == 1) {
                            date.format(DateTimeFormatter.ofPattern("d/M"))
                        } else {
                            date.format(DateTimeFormatter.ofPattern("d"))
                        }

                        it.nativeCanvas.drawText(
                            formattedDate,
                            barSpacing * index + 10f,
                            (start + 125).toFloat(), // Position below the bars
                            Paint().asFrameworkPaint().apply {
                                color = colorOnPrimary.toArgb()
                                textSize = 36f // Adjust text size as needed
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                            }
                        )
                    }
                }
            }

            // Draw Y-axis labels on the right side
            levels.forEach { i ->
                val yPos = start - i * heightFactor
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        i.toString(),
                        size.width + 50f,
                        yPos,
                        Paint().asFrameworkPaint().apply {
                            color = colorOnPrimary.toArgb()
                            textSize = 36f // Adjust text size as needed
                            textAlign = android.graphics.Paint.Align.RIGHT
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set text to bold
                        }
                    )
                }
            }
        }
    }
}



