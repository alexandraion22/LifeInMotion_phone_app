import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.bpm.Bpm
import com.example.healthapp.database.bpm.BpmRepository
import com.example.healthapp.service.toEpochMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsContent(bpmRepository: BpmRepository) {
    var bpmList by remember { mutableStateOf<List<Bpm>>(emptyList()) }

    val currentTime = LocalDateTime.now()
    val startOfHourEpoch = currentTime.withMinute(0).withSecond(0).toEpochMillis()
    val startOfNextHourEpoch = currentTime.plusHours(1).withMinute(0).withSecond(0).toEpochMillis()

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            bpmRepository.getAllPastHour(startOfHourEpoch,startOfNextHourEpoch)
        }
        bpmList = data
        Log.e("HERE", bpmList.toString())
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
fun BarChart(bpmList: List<Bpm>) {
    Canvas(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        val barWidth = 50F

        // Draw bars
        bpmList.forEachIndexed { index, bpm ->
            val barHeight = bpm.bpm.toFloat() * 2
            drawRoundRect(
                color = Color.Blue,
                topLeft = Offset(75F * index, 200 - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}

