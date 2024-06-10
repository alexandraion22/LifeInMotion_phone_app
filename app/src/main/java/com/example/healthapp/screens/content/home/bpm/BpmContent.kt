import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.Bpm
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.service.toEpochMillis
import com.example.healthapp.ui.theme.PsychedelicPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BpmContent(bpmHourlyRepository: BpmHourlyRepository, bpmDailyRepository: BpmDailyRepository, stepsHourlyRepository: StepsHourlyRepository, stepsDailyRepository: StepsDailyRepository) {
    var bpmList by remember { mutableStateOf<List<BpmHourly>>(emptyList()) }

    val currentTime = LocalDateTime.now()
    val startOfDay = currentTime.withHour(0).withMinute(0).withSecond(0).toEpochMillis()
    val startOfNextDay = currentTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).toEpochMillis()
    val startOfWeek = currentTime.minusDays(7).withHour(0).withMinute(0).withSecond(0).toEpochMillis()

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            bpmHourlyRepository.getAllPastDay(startOfDay,startOfNextDay)
        }
        val data2 = withContext(Dispatchers.IO) {
            bpmDailyRepository.getAllPast7days(startOfWeek, startOfDay)
        }
        val data3 = withContext(Dispatchers.IO) {
            stepsHourlyRepository.getAllPastDay(startOfDay,startOfNextDay)
        }
        val data4 = withContext(Dispatchers.IO) {
            stepsDailyRepository.getAllPast7days(startOfWeek, startOfDay)
        }

        Log.e("HERE", data.toString())
        Log.e("HERE2", data2.toString())
        Log.e("HERE3", data3.toString())
        Log.e("HERE4", data4.toString())
        bpmList = data
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bpmList.isNotEmpty()) {
            BarChart(bpmList)
        } else {
            Text(text = "No data available")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarChart(bpmList: List<BpmHourly>) {
    Canvas(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        val barWidth = 25F
        val maxBpm = bpmList.maxByOrNull { it.maxBpm }?.maxBpm ?: 0

        // Draw bars and legend
        bpmList.forEachIndexed { index, bpm ->
            val barUp = bpm.maxBpm.toFloat() * 5
            val barHeight = (bpm.maxBpm.toFloat() - bpm.minBpm.toFloat()) * 5
            drawRoundRect(
                color = PsychedelicPurple,
                topLeft = Offset(30F * index, 500 - barUp),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }

        for (i in 0 until maxBpm / 10 + 2) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    (i *10).toString(),
                    75F * 12 + barWidth + 8.dp.toPx(),
                    (500 - i * 50).toFloat(), // Adjust position to center the label
                    Paint().asFrameworkPaint().apply {
                        color = Color.Black.toArgb()
                        textSize = 20f // Adjust text size as needed
                    }
                )
            }
        }
    }
}

