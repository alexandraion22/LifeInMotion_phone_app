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
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


@SuppressLint("NewApi")
@Composable
fun IndividualSleepContent(
    sleepDailyRepository: SleepDailyRepository
) {

    val coroutineScope = rememberCoroutineScope()
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    var allSleeps by remember { mutableStateOf<List<SleepDaily>>(emptyList()) }
    var todaysSleep by remember { mutableStateOf<SleepDaily?>(null) }
    LaunchedEffect(currentTime) {
        val timeYesterday8Pm = currentTime.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis() - 240000
        val timeToday8Pm = currentTime.withHour(20).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        allSleeps = withContext(Dispatchers.IO) {
            sleepDailyRepository.getEntriesForDay(timeYesterday8Pm,timeToday8Pm)
        }
        todaysSleep = if(allSleeps.isNotEmpty())
            allSleeps[0]
        else
            null
        if(todaysSleep!=null){
            calculateSleepScore(
                todaysSleep!!.REMDuration,
                todaysSleep!!.deepDuration,
                todaysSleep!!.lightDuration,
                object : SleepScoreCallback {
                    override fun onSleepScoreCalculated(score: Int) {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                sleepDailyRepository.updateAutomaticScore(todaysSleep!!.id, score)
                                todaysSleep = sleepDailyRepository.getEntriesForDay(timeYesterday8Pm, timeToday8Pm)[0]
                            }
                        }
                    }
                })
        }
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

private fun calculateSleepScore(remDuration: Int, deepDuration: Int, lightDuration: Int, callback: SleepScoreCallback) {
    val conditions = CustomModelDownloadConditions.Builder()
        .requireWifi()
        .build()

    FirebaseModelDownloader.getInstance()
        .getModel(
            "sleep-efficiency", DownloadType.LATEST_MODEL,
            conditions
        )
        .addOnSuccessListener { model: CustomModel? ->
            val modelFile = model?.file
            if (modelFile != null) {
                val interpreter = Interpreter(modelFile)
                val totalDuration = remDuration + deepDuration + lightDuration
                val remPercentage = remDuration / totalDuration.toFloat() * 100
                val deepPercentage = deepDuration / totalDuration.toFloat() * 100
                val lightPercentage = lightDuration / totalDuration.toFloat() * 100
                val inputData = floatArrayOf(
                    totalDuration / 60f,
                    remPercentage,
                    deepPercentage,
                    lightPercentage
                )

                // Convert the input data to ByteBuffer
                val inputBuffer = ByteBuffer.allocateDirect(4 * inputData.size)
                inputBuffer.order(ByteOrder.nativeOrder())
                inputBuffer.asFloatBuffer().put(inputData)

                // Allocate buffer for the output (single float)
                val bufferSize = 4 // Size of a float in bytes
                val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

                // Run the model
                interpreter.run(inputBuffer, modelOutput)

                // Rewind and extract the float output
                modelOutput.rewind()
                val outputData = modelOutput.asFloatBuffer().get(0)
                Log.e("TAG", outputData.toString())
                val returnVal = (outputData * 100 + 50).toInt()
                callback.onSleepScoreCalculated(returnVal)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("ModelDownload", "Failed to download model", exception)
            callback.onSleepScoreCalculated(-1) // Indicate an error in some way
        }
}

interface SleepScoreCallback {
    fun onSleepScoreCalculated(score: Int)
}