package com.example.healthapp.service

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.healthapp.database.activity.ActivityDaily
import com.example.healthapp.database.activity.ActivityDailyRepository
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.Bpm
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.calories.CaloriesDaily
import com.example.healthapp.database.calories.CaloriesDailyRepository
import com.example.healthapp.database.state.State
import com.example.healthapp.database.state.StateRepository
import com.example.healthapp.database.steps.daily.StepsDaily
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourly
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import com.example.healthapp.database.workouts.Workout
import com.example.healthapp.database.workouts.WorkoutRepository
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
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

    @Inject
    lateinit var stepsHourlyRepository: StepsHourlyRepository

    @Inject
    lateinit var stepsDailyRepository: StepsDailyRepository

    @Inject
    lateinit var workoutRepository: WorkoutRepository

    @Inject
    lateinit var stateRepository: StateRepository

    @Inject
    lateinit var activityDailyRepository: ActivityDailyRepository

    @Inject
    lateinit var caloriesDailyRepository: CaloriesDailyRepository

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == BPM_PATH) {
            handleReceivedBpm(messageEvent.data)
        }
        if (messageEvent.path == STEPS_PATH) {
            handleReceivedSteps(messageEvent.data)
        }
        if (messageEvent.path == WORKOUT_PATH) {
            handleReceivedWorkout(messageEvent.data)
        }
        if (messageEvent.path == STARTED_WORKOUT_PATH) {
            handleStartedWorkout(messageEvent.data)
        }

    }

    private fun handleStartedWorkout(data: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            val previousEntry = stateRepository.getFirst()
            if(previousEntry == null)
                stateRepository.insert(State(isSleeping = false,
                    isWorkingOutBpm = false,
                    isWorkingOutWatch = true,
                    stepsLast8Minutes = 0,
                    stepsLast4Minutes = 0,
                    timestampLastSteps = 0,
                    caloriesConsumedBpm = 0))
            else
                stateRepository.updateIsWorkingOutWatch(previousEntry.id,true)
        }
    }

    @SuppressLint("NewApi")
    private fun handleReceivedWorkout(data: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            val previousEntry = stateRepository.getFirst()
            if (previousEntry != null)
                stateRepository.updateIsWorkingOutWatch(previousEntry.id,false)
        }

        val message = String(data)
        val datetime = message.split('|')[0]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val timestamp = LocalDateTime.parse(datetime, formatter)

        val avgHR = (message.split('|')[1]).toInt()
        val minHr = (message.split('|')[2]).toInt()
        val maxHr = (message.split('|')[3]).toInt()
        val calories = (message.split('|')[4]).toInt()
        val duration = (message.split('|')[5]).toLong()
        val type = (message.split('|')[6])
        if (calories == 0)
            return

        updateActiveTimeAndCalories(duration,calories,timestamp)
        CoroutineScope(Dispatchers.IO).launch {
            workoutRepository.insert(
                Workout(
                    meanHR = avgHR,
                    minHR = minHr,
                    maxHR = maxHr,
                    duration = duration,
                    calories = calories,
                    type = type,
                    autoRecorder = false,
                    confirmed = true,
                    timestamp = timestamp.toEpochMillis() - duration
                )
            )
            val startOfDay = timestamp.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
            val endOfDay = timestamp.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusNanos(1).toEpochMillis()
            Log.e("TAG",workoutRepository.getEntriesForDay(startOfDay, endOfDay = endOfDay).toString())
        }
    }

    @SuppressLint("NewApi")
    fun Long.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(this / 1000, 0, ZoneOffset.UTC)
    }

    @SuppressLint("NewApi")
    private fun updateActiveTimeAndCalories(duration: Long, calories: Int, timeStamp: LocalDateTime) {
        CoroutineScope(Dispatchers.IO).launch {
            val beginingMilis= timeStamp.toEpochMillis() - duration
            val startOfDay = beginingMilis.toLocalDateTime().withHour(0).withMinute(0).withSecond(0).withNano(0)
            activityDailyRepository.deleteAll()
            activityDailyRepository.insert(ActivityDaily(timestamp = startOfDay.toEpochMillis(), activeTime = (duration/60000).toInt()))
            caloriesDailyRepository.deleteAll()
            caloriesDailyRepository.insert(CaloriesDaily(timestamp = startOfDay.toEpochMillis(), totalCalories = calories))
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

        CoroutineScope(Dispatchers.IO).launch {
            bpmRepository.deleteAllBpms() // delete all the previous entries
            bpmRepository.insert(bpmData) // insert data in the frequent db
            bpmUpdateOrCreateHourlyEntry(bpm, timestamp)
            bpmUpdateOrCreateDailyEntry(bpm, timestamp)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleReceivedSteps(data: ByteArray) {
        val message = String(data)
        val datetime = message.split('|')[0]
        val steps = (message.split('|')[1]).toInt()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val timestamp = LocalDateTime.parse(datetime, formatter)

        CoroutineScope(Dispatchers.IO).launch {
            stepsUpdateOrCreateHourlyEntry(steps, timestamp)
            stepsUpdateOrCreateDailyEntry(steps, timestamp)

            val previousEntry = stateRepository.getFirst()
            if (previousEntry != null)
            {
                if(timestamp.minusMinutes(5).toEpochMillis() < previousEntry.timestampLastSteps) {
                    stateRepository.updateStepsLast8Minutes(previousEntry.id,previousEntry.stepsLast4Minutes)
                    stateRepository.updateStepsLast4Minutes(previousEntry.id,steps)
                    stateRepository.updateTimestampLastSteps(previousEntry.id,timestamp.toEpochMillis())
                }

            }
            else stateRepository.insert(State(isSleeping = false,
                isWorkingOutBpm = false,
                isWorkingOutWatch = true,
                stepsLast8Minutes = 0,
                stepsLast4Minutes = steps,
                timestampLastSteps = timestamp.toEpochMillis(),
                caloriesConsumedBpm = 0))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun stepsUpdateOrCreateHourlyEntry(currentSteps: Int, timestamp: LocalDateTime) {
        val startOfHour = timestamp.withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        val existingEntry = stepsHourlyRepository.getEntryForHour(startOfHour)

        if (existingEntry == null) {
            val newEntry = StepsHourly(
                timestamp = startOfHour,
                steps = currentSteps
            )
            stepsHourlyRepository.insert(newEntry)
        } else {
            val updatedEntry = existingEntry.copy(
                steps = existingEntry.steps + currentSteps
            )
            stepsHourlyRepository.update(updatedEntry)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun stepsUpdateOrCreateDailyEntry(currentSteps: Int, timestamp: LocalDateTime) {
        val startOfDay = timestamp.withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochMillis()
        val existingEntry = stepsDailyRepository.getEntryForDay(startOfDay)

        if (existingEntry == null) {
            val newEntry = StepsDaily(
                timestamp = startOfDay,
                steps = currentSteps
            )
            stepsDailyRepository.insert(newEntry)
        } else {
            // Update existing entry
            val updatedEntry = existingEntry.copy(
                steps = existingEntry.steps + currentSteps
            )
            stepsDailyRepository.update(updatedEntry)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun bpmUpdateOrCreateHourlyEntry(bpm: Int, timestamp: LocalDateTime) {
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
    private suspend fun bpmUpdateOrCreateDailyEntry(bpm: Int, timestamp: LocalDateTime) {
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
        private const val STEPS_PATH ="/steps"
        private const val WORKOUT_PATH ="/workout"
        private const val STARTED_WORKOUT_PATH ="/started_workout"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
