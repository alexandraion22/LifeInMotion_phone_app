package com.example.healthapp.screens.content.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.example.healthapp.database.bpm.Bpm
import com.example.healthapp.database.bpm.BpmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsContent(bpmRepository: BpmRepository) {
    var bpmList by remember { mutableStateOf<List<Bpm>>(emptyList()) }

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            bpmRepository.getAllPastHour()
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(bpmList) { bpm ->
                    BpmRow(bpm)
                }
            }
        } else {
            Text(text = "No data available")
        }
    }
}

@Composable
fun BpmRow(bpm: Bpm) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "ID: ${bpm.bpm}")
        Text(text = "Timestamp: ${bpm.timestamp}")
    }
}
