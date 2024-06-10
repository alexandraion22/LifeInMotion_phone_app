package com.example.healthapp.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyDao
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.Bpm
import com.example.healthapp.database.bpm.last.BpmRepository
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class WatchListenerService: WearableListenerService() {

    @Inject
    lateinit var bpmRepository: BpmRepository

    @Inject
    lateinit var bpmHourlyRepository: BpmHourlyRepository

    @Inject
    lateinit var bpmDailyRepository: BpmDailyRepository

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == BPM_PATH) {
            handleReceivedBpm(messageEvent.data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleReceivedBpm(data: ByteArray) {
        val message = String(data)
        val datetime = message.split('|')[0]
        val bpm = (message.split('|')[1]).toInt()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val timestamp = LocalDateTime.parse(datetime, formatter)

        val bpmData = Bpm(
            bpm = bpm,
            timestamp = timestamp.toEpochMillis()
        )
        Log.d(TAG, "Finished processing")
        CoroutineScope(Dispatchers.IO).launch {
            bpmRepository.deleteAllBpms() // delete all the previous entries
            bpmRepository.insert(bpmData) // insert data in the frequent db
            updateOrCreateHourlyEntry(bpm, timestamp)
            updateOrCreateDailyEntry(bpm, timestamp)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateOrCreateHourlyEntry(bpm: Int, timestamp: LocalDateTime) {
        val startOfHour = timestamp.withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        val existingEntry = bpmHourlyRepository.getEntryForHour(startOfHour)

        if (existingEntry == null) {
            val newEntry = BpmHourly(
                timestamp = startOfHour,
                maxBpm = bpm,
                minBpm = bpm
            )
            bpmHourlyRepository.insert(newEntry)
        } else {
            // Update existing entry
            val updatedEntry = existingEntry.copy(
                maxBpm = maxOf(existingEntry.maxBpm, bpm),
                minBpm = minOf(existingEntry.minBpm, bpm)
            )
            bpmHourlyRepository.update(updatedEntry)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateOrCreateDailyEntry(bpm: Int, timestamp: LocalDateTime) {
        val startOfDay = timestamp.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        val existingEntry = bpmDailyRepository.getEntryForDay(startOfDay)

        if (existingEntry == null) {
            val newEntry = BpmDaily(
                timestamp = startOfDay,
                maxBpm = bpm,
                minBpm = bpm
            )
            bpmDailyRepository.insert(newEntry)
        } else {
            // Update existing entry
            val updatedEntry = existingEntry.copy(
                maxBpm = maxOf(existingEntry.maxBpm, bpm),
                minBpm = minOf(existingEntry.minBpm, bpm)
            )
            bpmDailyRepository.update(updatedEntry)
        }
    }


    companion object{
        private const val TAG = "WatchListenerService"
        private const val BPM_PATH = "/bpm"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
