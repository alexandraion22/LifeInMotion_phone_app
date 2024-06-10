import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.PsychedelicPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BpmContent(
    bpmHourlyRepository: BpmHourlyRepository,
    bpmDailyRepository: BpmDailyRepository,
    stepsHourlyRepository: StepsHourlyRepository,
    stepsDailyRepository: StepsDailyRepository
) {
    var bpmList by remember { mutableStateOf<List<BpmHourly>>(emptyList()) }
    var selectedButton by remember { mutableIntStateOf(1) } // Initial selection is the first button

    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val startOfNextDay =
        currentTime.withHour(0).withMinute(0).withSecond(0).toEpochMillis()
    val startOfWeek =
        currentTime.minusDays(7).withHour(0).withMinute(0).withSecond(0).toEpochMillis()

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            bpmHourlyRepository.getAllPastDay(startOfDay, startOfNextDay)
        }
        Log.e("HERE", data.toString())
        bpmList = data
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .fillMaxHeight(0.075f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    FilledTonalButton(
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 1) colors.primary else Color.LightGray),
                        onClick = { selectedButton = 1 }
                    ) {
                        Text("Today",
                            color = colors.onPrimary
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .width(115.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 2) colors.primary else Color.LightGray),
                        onClick = { selectedButton = 2 }
                    ) {
                        Text("Past week",
                            color = colors.onPrimary
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .width(130.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == 3) colors.primary else Color.LightGray),
                        onClick = { selectedButton = 3 }
                    ) {
                        Text("Past month",
                            color = colors.onPrimary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                BarChart(bpmList)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarChart(bpmList: List<BpmHourly>) {
    // Create a map with all hours (0 to 23) initialized to null
    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
    val hourlyData = (0..23).associateWith { bpmList.find { bpm -> (bpm.timestamp - startOfDay).toInt() == it * 3600000 } }

    Log.e("HERE", hourlyData.toString())
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val barWidth = 20F
        val barSpacing = 35F
        val start = 1400
        val factor = 6

        // Draw horizontal dotted lines
        val bpmLevels = listOf(40, 80, 120, 160, 200)
        val paint = Paint().asFrameworkPaint().apply {
            color = Color.Gray.toArgb()
            strokeWidth = 2f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
        }

        bpmLevels.forEach { bpmLevel ->
            val yPos = start - bpmLevel * factor
            drawIntoCanvas {
                it.nativeCanvas.drawLine(
                    0f,
                    yPos.toFloat(),
                    size.width - 30f,
                    yPos.toFloat(),
                    paint
                )
            }
        }

        // Draw bars and legend
        hourlyData.forEach { (hour, bpm) ->
            bpm?.let {
                val barUp = it.maxBpm.toFloat() * factor
                val barHeight = (it.maxBpm.toFloat() - it.minBpm.toFloat()) * factor
                drawRoundRect(
                    color = PsychedelicPurple,
                    topLeft = Offset(barSpacing * hour, start - barUp),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }

            // Draw hour labels
            if (hour % 3 == 0)
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "$hour",
                        barSpacing * hour,
                        start.toFloat(), // Position below the bars
                        Paint().asFrameworkPaint().apply {
                            color = Color.Black.toArgb()
                            textSize = 20f // Adjust text size as needed
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
        }

        // Draw Y-axis labels
        for (i in 0 until 23) {
            val bpmValue = i * 10
            val yPos = start - bpmValue * factor

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    (i * 10).toString(),
                    barSpacing * 24,
                    yPos.toFloat(),
                    Paint().asFrameworkPaint().apply {
                        color = Color.Black.toArgb()
                        textSize = 20f // Adjust text size as needed
                    }
                )
            }
        }
    }
}
