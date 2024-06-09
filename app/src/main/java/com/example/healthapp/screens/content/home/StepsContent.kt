package com.example.healthapp.screens.content.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.bpm.Bpm
import com.example.healthapp.database.bpm.BpmViewModel
import com.example.healthapp.database.users.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsContent(bpmViewModel: BpmViewModel) {
    // Your UI for the new screen
    val scope = rememberCoroutineScope()
    var bpmList by remember { mutableStateOf<List<Bpm>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            bpmList = bpmViewModel.getAllPastHour()
            Log.e(TAG, bpmViewModel.getAllPastHour().toString())
        }
    }

    // Your UI for the new screen
    Box(
        modifier = Modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bpmList.isNotEmpty()) {
            // Display BPM data
            bpmList.forEach { bpm ->
                Text(text = "BPM: ${bpm.bpm}, Time: ${bpm.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}")
            }
        } else {
            Text(text = "No BPM data available for the past hour")
        }
    }
}
