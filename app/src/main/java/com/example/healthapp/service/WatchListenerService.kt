package com.example.healthapp.service

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.healthapp.database.sleep.SleepDaily
import com.example.healthapp.database.sleep.SleepDailyRepository
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
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.random.Random

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

    @Inject
    lateinit var sleepDailyRepository: SleepDailyRepository
    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.e("TAG", String(messageEvent.data))
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
            stateRepository.updateIsWorkingOutWatch(previousEntry.id, true)
        }
    }

    @SuppressLint("NewApi")
    private fun handleReceivedWorkout(data: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            val previousEntry = stateRepository.getFirst()
            stateRepository.updateIsWorkingOutWatch(previousEntry.id, false)
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

        updateActiveTimeAndCalories(duration, calories)
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
        }
    }

    @SuppressLint("NewApi")
    private fun updateActiveTimeAndCalories(duration: Long, calories: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
            val activityDaily = activityDailyRepository.getEntryForDay(startOfDay.toEpochMillis())
            val caloriesDaily = caloriesDailyRepository.getEntryForDay(startOfDay.toEpochMillis())
            val activeBefore = activityDaily?.activeTime ?: 0
            val caloriesBefore = caloriesDaily?.totalCalories ?: 0
            activityDailyRepository.update(
                ActivityDaily(
                    timestamp = startOfDay.toEpochMillis(),
                    activeTime = (duration / 60000).toInt().coerceAtMost(30) + activeBefore
                )
            )
            caloriesDailyRepository.update(
                CaloriesDaily(
                    timestamp = startOfDay.toEpochMillis(),
                    totalCalories = calories + caloriesBefore
                )
            )
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
            var state = stateRepository.getFirst()
            Log.e("STATE", "Checking the state of the user")
            Log.e("STATE", state.toString())

            if (state.stepsLast4Minutes != 0 || state.stepsLast8Minutes != 0) {
                if (state.timestampLastSteps < LocalDateTime.now().minusMinutes(10)
                        .toEpochMillis()
                ) {
                    stateRepository.updateStepsLast4Minutes(state.id, 0)
                    stateRepository.updateStepsLast8Minutes(state.id, 0)
                } else
                    if (state.timestampLastSteps < LocalDateTime.now().minusMinutes(5)
                            .toEpochMillis()
                    ) {
                        stateRepository.updateStepsLast8Minutes(state.id, state.stepsLast4Minutes)
                        stateRepository.updateStepsLast4Minutes(state.id, 0)
                        stateRepository.updateTimestampLastSteps(
                            state.id,
                            LocalDateTime.now().toEpochMillis()
                        )
                    }
                state = stateRepository.getFirst()
            }

            // If the user is not working out, check if he's sleeping
            sleepCheck(bpm, state)
            state = stateRepository.getFirst()

            // If the user is not sleeping after the check, check if he's active
            if (!state.isWorkingOutWatch) {
                if (bpm > 0.5f * 198 && bpmRepository.getFirst().bpm > 0.5f * 198) {
                    // Start a workout if neither is active
                    if (!state.isWorkingOutBpm && !state.isWalking) {
                        Log.e("BIG HR and no workout", state.toString())
                        stateRepository.updateMaxBpmWorkout(state.id, bpm)
                        stateRepository.updateMinBpmWorkout(state.id, bpm)
                        stateRepository.updateTimestampStartedWorkout(
                            state.id,
                            LocalDateTime.now().toEpochMillis()
                        )
                        if (state.stepsLast8Minutes + state.stepsLast4Minutes > 400) {
                            stateRepository.updateIsWalking(state.id, true)
                            stateRepository.updateCaloriesConsumedBpm(
                                state.id,
                                (0.035 * (state.stepsLast8Minutes + state.stepsLast4Minutes)).toInt()
                            )
                        } else {
                            stateRepository.updateIsWorkingOutBpm(state.id, true)
                            stateRepository.updateCaloriesConsumedBpm(
                                state.id,
                                calculateCalories(bpmRepository.getFirst().bpm, bpm)
                            )
                        }
                    } else {
                        // Continue a workout
                        Log.e("BIG HR and workout", state.toString())
                        if (state.maxBpmWorkout < bpm)
                            stateRepository.updateMaxBpmWorkout(state.id, bpm)
                        if (state.minBpmWorkout > bpm)
                            stateRepository.updateMinBpmWorkout(state.id, bpm)
                        if (state.isWalking) {
                            stateRepository.updateCaloriesConsumedBpm(
                                state.id,
                                (0.035 * (state.stepsLast4Minutes)).toInt() + state.caloriesConsumedBpm
                            )
                        }
                        if (state.isWorkingOutBpm) {
                            stateRepository.updateCaloriesConsumedBpm(
                                state.id,
                                calculateCalories(
                                    bpmRepository.getFirst().bpm,
                                    bpm
                                ) + state.caloriesConsumedBpm
                            )
                        }
                    }
                } else {
                    // Update the bpm
                    if (state.maxBpmWorkout < bpm)
                        stateRepository.updateMaxBpmWorkout(state.id, bpm)
                    if (state.minBpmWorkout > bpm)
                        stateRepository.updateMinBpmWorkout(state.id, bpm)
                    state = stateRepository.getFirst()

                    if (state.isWorkingOutBpm) {
                        try {
                            getWorkoutType(
                                state.minBpmWorkout,
                                state.maxBpmWorkout,
                                (state.minBpmWorkout + state.maxBpmWorkout) / 2,
                                LocalDateTime.now().toEpochMillis() - state.timestampStartWorkout,
                                state.caloriesConsumedBpm,
                                object : WorkoutTypeCallback {
                                    override fun onWorkoutTypeDetermined(workoutType: String) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            workoutRepository.insert(
                                                Workout(
                                                    meanHR = (state.minBpmWorkout + state.maxBpmWorkout) / 2,
                                                    minHR = state.minBpmWorkout,
                                                    maxHR = state.maxBpmWorkout,
                                                    duration = LocalDateTime.now()
                                                        .toEpochMillis() - state.timestampStartWorkout,
                                                    calories = state.caloriesConsumedBpm,
                                                    type = workoutType,
                                                    autoRecorder = true,
                                                    confirmed = false,
                                                    timestamp = state.timestampStartWorkout
                                                )
                                            )
                                            updateActiveTimeAndCalories(
                                                LocalDateTime.now()
                                                    .toEpochMillis() - state.timestampStartWorkout,
                                                state.caloriesConsumedBpm
                                            )
                                            stateRepository.updateCaloriesConsumedBpm(state.id, 0)
                                            stateRepository.updateIsWorkingOutBpm(state.id, false)
                                            stateRepository.updateTimestampStartedWorkout(
                                                state.id,
                                                0
                                            )
                                            stateRepository.updateMinBpmWorkout(state.id, 220)
                                            stateRepository.updateMaxBpmWorkout(state.id, 0)
                                        }
                                    }
                                    override fun onError(exception: Exception) {
                                        Log.e(
                                            "WorkoutTypeError",
                                            "Failed to determine workout type",
                                            exception
                                        )
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            Log.e("Error", "Exception occurred", e)
                        }
                        updateActiveTimeAndCalories(
                            LocalDateTime.now().toEpochMillis() - state.timestampStartWorkout,
                            state.caloriesConsumedBpm
                        )
                        stateRepository.updateCaloriesConsumedBpm(state.id, 0)
                        stateRepository.updateIsWorkingOutBpm(state.id, false)
                        stateRepository.updateTimestampStartedWorkout(state.id, 0)
                        stateRepository.updateMinBpmWorkout(state.id, 220)
                        stateRepository.updateMaxBpmWorkout(state.id, 0)
                    } else {
                        if (state.maxBpmWorkout < bpm)
                            stateRepository.updateMaxBpmWorkout(state.id, bpm)
                        if (state.minBpmWorkout > bpm)
                            stateRepository.updateMinBpmWorkout(state.id, bpm)
                        state = stateRepository.getFirst()

                        if (state.stepsLast8Minutes + state.stepsLast4Minutes > 400) {
                            if (state.isWalking)
                                stateRepository.updateCaloriesConsumedBpm(
                                    state.id,
                                    (0.035 * state.stepsLast4Minutes).toInt() + state.caloriesConsumedBpm
                                )
                            else {
                                stateRepository.updateMinBpmWorkout(state.id, bpm)
                                stateRepository.updateMaxBpmWorkout(state.id, bpm)
                                stateRepository.updateIsWalking(state.id, true)
                                stateRepository.updateTimestampStartedWorkout(
                                    state.id,
                                    LocalDateTime.now().toEpochMillis()
                                )
                                stateRepository.updateCaloriesConsumedBpm(
                                    state.id,
                                    (0.035 * (state.stepsLast8Minutes + state.stepsLast4Minutes)).toInt()
                                )
                            }
                        } else if (state.isWalking) {
                            workoutRepository.insert(
                                Workout(
                                    meanHR = (state.minBpmWorkout + state.maxBpmWorkout) / 2,
                                    minHR = state.minBpmWorkout,
                                    maxHR = state.maxBpmWorkout,
                                    duration = LocalDateTime.now()
                                        .toEpochMillis() - state.timestampStartWorkout,
                                    calories = state.caloriesConsumedBpm,
                                    type = "walk",
                                    autoRecorder = true,
                                    confirmed = false,
                                    timestamp = state.timestampStartWorkout
                                )
                            )
                            updateActiveTimeAndCalories(
                                LocalDateTime.now().toEpochMillis() - state.timestampStartWorkout,
                                state.caloriesConsumedBpm
                            )
                            stateRepository.updateCaloriesConsumedBpm(state.id, 0)
                            stateRepository.updateIsWalking(state.id, false)
                            stateRepository.updateTimestampStartedWorkout(state.id, 0)
                            stateRepository.updateMinBpmWorkout(state.id, 220)
                            stateRepository.updateMaxBpmWorkout(state.id, 0)
                        }
                    }
                }
            }

            // Update the  bpm
            bpmRepository.deleteAllBpms() // delete all the previous entries
            bpmRepository.insert(bpmData) // insert data in the frequent db
            bpmUpdateOrCreateHourlyEntry(bpm, timestamp)
            bpmUpdateOrCreateDailyEntry(bpm, timestamp)
        }
    }

    private fun getWorkoutType(
        minBpmWorkout: Int,
        maxBpmWorkout: Int,
        meanBpmWorkout: Int,
        duration: Long,
        caloriesConsumedBpm: Int,
        callback: WorkoutTypeCallback
    ) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("workout-classifier", DownloadType.LATEST_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    val interpreter = Interpreter(modelFile)
                    val inputData = floatArrayOf(
                        meanBpmWorkout / 198f,
                        minBpmWorkout / 198f,
                        maxBpmWorkout / 198f,
                        (caloriesConsumedBpm / (duration / 60000)).toFloat()
                    )
                    val inputBuffer = ByteBuffer.allocateDirect(4 * inputData.size)
                    inputBuffer.order(ByteOrder.nativeOrder())
                    inputBuffer.asFloatBuffer().put(inputData)
                    val bufferSize = 4 * java.lang.Float.SIZE / java.lang.Byte.SIZE
                    val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
                    interpreter.run(inputBuffer, modelOutput)
                    modelOutput.rewind()
                    val outputShape = intArrayOf(1, 4)
                    val outputData = FloatArray(outputShape[1])
                    modelOutput.asFloatBuffer().get(outputData)
                    outputData.forEach { Log.e("HERE", it.toString()) }
                    val workoutType = when (outputData.indices.maxByOrNull { outputData[it] } ?: -1) {
                        0 -> "aerobic"
                        1 -> "circuit_training"
                        2 -> "pilates"
                        3 -> "weights"
                        else -> "unknown"
                    }
                    callback.onWorkoutTypeDetermined(workoutType)
                } else {
                    callback.onError(Exception("Model file is null"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ModelDownload", "Failed to download model", exception)
                callback.onError(exception)
            }
    }

    interface WorkoutTypeCallback {
        fun onWorkoutTypeDetermined(workoutType: String)
        fun onError(exception: Exception)
    }


    private fun calculateCalories(bpm: Int, bpm1: Int): Int {
        return (bpm + bpm1) / 8
    }

    private fun finishSleep(state: State) {
        CoroutineScope(Dispatchers.IO).launch {
            stateRepository.updateSleepCycle(state.id, 0)
            stateRepository.updateSleepStage(state.id, 0)
            stateRepository.updateTimeDeepSleep(state.id, 0)
            stateRepository.updateTimeLightSleep(state.id, 0)
            stateRepository.updateTimeREM(state.id, 0)
            stateRepository.updateIsSleeping(state.id, false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun sleepCheck(bpm: Int, state: State) {
        Log.e("SLEEP", "Entered check sleep with bpm:$bpm")
        Log.e("SLEEP", "Current state:$state")
        CoroutineScope(Dispatchers.IO).launch {
            val maxBpm = 198
            when (state.sleepStage) {
                0 -> { // Awake
                    if (bpm < (0.35 * maxBpm).toInt()) {
                        Log.e("SLEEP", "Entered N1 with bpm:$bpm")
                        stateRepository.updateIsSleeping(state.id, true)
                        stateRepository.updateSleepStage(state.id, 1)
                        stateRepository.updateTimeLightSleep(state.id, state.timeLightSleep + 5)
                    }
                }

                1 -> { // N1 - Light Sleep
                    when {
                        bpm in (0.3 * maxBpm).toInt()..(0.35 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Continue N1 with bpm:$bpm")
                            stateRepository.updateTimeLightSleep(state.id, state.timeLightSleep + 5)
                        }

                        bpm > (0.45 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Woke up from N1 with bpm:$bpm")
                            if (state.sleepCycle > 0) {
                                Log.e("SLEEP", "Add sleep to database")

                                val startOfSleep = LocalDateTime.now().toEpochMillis() - (state.timeDeepSleep + state.timeLightSleep + state.timeREM) * 60000
                                calculateSleepScore(state.timeREM, state.timeDeepSleep, state.timeLightSleep, object : SleepScoreCallback {
                                    override fun onSleepScoreCalculated(score: Int) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            sleepDailyRepository.insert(
                                                SleepDaily(
                                                    timestampStart = startOfSleep,
                                                    REMDuration = state.timeREM,
                                                    lightDuration = state.timeLightSleep,
                                                    deepDuration = state.timeDeepSleep,
                                                    cycles = state.sleepCycle,
                                                    givenScore = 0,
                                                    automaticScore = score,
                                                    awakenings = Random.nextInt(1, 5)
                                                )
                                            )
                                        }
                                        finishSleep(state)
                                    }
                                    override fun onError(exception: Exception) {
                                        Log.e("SleepScoreError", "Failed to calculate sleep score", exception)
                                    }
                                })
                            }
                            finishSleep(state)
                        }

                        bpm < (0.3 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Transition from N1 to N2 with bpm:$bpm")
                            stateRepository.updateSleepStage(state.id, 2)
                            stateRepository.updateTimeDeepSleep(state.id, state.timeDeepSleep + 5)
                        }

                        bpm in (0.35 * maxBpm).toInt()..(0.45 * maxBpm).toInt() -> {
                            if (state.timeDeepSleep != 0) {
                                // Only move to REM, if the user has been in deep sleep
                                Log.e("SLEEP", "Transition from N1 to REM with bpm:$bpm")
                                stateRepository.updateSleepStage(state.id, 3)
                                stateRepository.updateSleepCycle(state.id, state.sleepCycle + 1)
                                stateRepository.updateTimeREM(state.id, state.timeREM + 5)
                            } else {
                                Log.e("SLEEP", "Woke up from N1 with bpm:$bpm")
                                finishSleep(state)
                            }
                        }
                    }
                }

                2 -> { // N2 - Deep Sleep
                    when {
                        bpm < (0.3 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Continue N2 with bpm:$bpm")
                            stateRepository.updateTimeDeepSleep(state.id, state.timeDeepSleep + 5)
                        }

                        bpm > (0.45 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Woke up from N2 with bpm:$bpm")
                            if (state.sleepCycle > 0) {
                                Log.e("SLEEP", "Add sleep to database")
                                val startOfSleep = LocalDateTime.now()
                                    .toEpochMillis() - (state.timeDeepSleep + state.timeLightSleep + state.timeREM) * 60000
                                calculateSleepScore(state.timeREM, state.timeDeepSleep, state.timeLightSleep, object : SleepScoreCallback {
                                    override fun onSleepScoreCalculated(score: Int) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            sleepDailyRepository.insert(
                                                SleepDaily(
                                                    timestampStart = startOfSleep,
                                                    REMDuration = state.timeREM,
                                                    lightDuration = state.timeLightSleep,
                                                    deepDuration = state.timeDeepSleep,
                                                    cycles = state.sleepCycle,
                                                    givenScore = 0,
                                                    automaticScore = score,
                                                    awakenings = Random.nextInt(1, 5)
                                                )
                                            )
                                        }
                                        finishSleep(state)
                                    }
                                    override fun onError(exception: Exception) {
                                        Log.e("SleepScoreError", "Failed to calculate sleep score", exception)
                                    }
                                })
                            }
                            finishSleep(state)
                        }

                        bpm in (0.3 * maxBpm).toInt()..(0.45 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Transition from N2 to N1 with bpm:$bpm")
                            stateRepository.updateSleepStage(state.id, 1)
                            stateRepository.updateTimeLightSleep(state.id, state.timeLightSleep + 5)
                        }
                    }
                }

                3 -> { // REM
                    when {
                        bpm in (0.35 * maxBpm).toInt()..(0.45 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Continue REM with bpm:$bpm")
                            stateRepository.updateTimeREM(state.id, state.timeREM + 5)
                        }

                        bpm > (0.45 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Woke up from REM with bpm:$bpm")
                            if (state.sleepCycle > 0) {
                                Log.e("SLEEP", "Add sleep to database")
                                val startOfSleep = LocalDateTime.now()
                                    .toEpochMillis() - (state.timeDeepSleep + state.timeLightSleep + state.timeREM) * 60000
                                calculateSleepScore(state.timeREM, state.timeDeepSleep, state.timeLightSleep, object : SleepScoreCallback {
                                    override fun onSleepScoreCalculated(score: Int) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            sleepDailyRepository.insert(
                                                SleepDaily(
                                                    timestampStart = startOfSleep,
                                                    REMDuration = state.timeREM,
                                                    lightDuration = state.timeLightSleep,
                                                    deepDuration = state.timeDeepSleep,
                                                    cycles = state.sleepCycle,
                                                    givenScore = 0,
                                                    automaticScore = score,
                                                    awakenings = Random.nextInt(1, 5)
                                                )
                                            )
                                        }
                                        finishSleep(state)
                                    }
                                    override fun onError(exception: Exception) {
                                        Log.e("SleepScoreError", "Failed to calculate sleep score", exception)
                                    }
                                })
                            }
                            finishSleep(state)
                        }

                        bpm < (0.35 * maxBpm).toInt() -> {
                            Log.e("SLEEP", "Transition from REM to N1 with bpm:$bpm")
                            stateRepository.updateSleepStage(state.id, 1)
                            stateRepository.updateTimeLightSleep(state.id, state.timeLightSleep + 5)
                        }
                    }
                }
            }
        }
    }

    interface SleepScoreCallback {
        fun onSleepScoreCalculated(score: Int)
        fun onError(exception: Exception)
    }

    private fun calculateSleepScore(remDuration: Int, deepDuration: Int, lightDuration: Int, callback: SleepScoreCallback) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("sleep-efficiency", DownloadType.LATEST_MODEL, conditions)
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
                    val sleepScore = (outputData * 100 + 60).toInt()
                    callback.onSleepScoreCalculated(sleepScore)
                } else {
                    callback.onError(Exception("Model file is null"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ModelDownload", "Failed to download model", exception)
                callback.onError(exception)
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
            if(timestamp.minusMinutes(5).toEpochMillis() < previousEntry.timestampLastSteps) {
                stateRepository.updateStepsLast8Minutes(previousEntry.id,previousEntry.stepsLast4Minutes)
                stateRepository.updateStepsLast4Minutes(previousEntry.id,steps)
                stateRepository.updateTimestampLastSteps(previousEntry.id,timestamp.toEpochMillis())
            }
            else
            {
                stateRepository.updateStepsLast8Minutes(previousEntry.id,0)
                stateRepository.updateStepsLast4Minutes(previousEntry.id,steps)
                stateRepository.updateTimestampLastSteps(previousEntry.id,timestamp.toEpochMillis())
            }
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

fun returnNewState(isSleeping: Boolean = false,
                   sleepStage: Int = 0,
                   sleepCycle: Int = 0,
                   timeLightSleep: Int = 0,
                   timeDeepSleep: Int = 0,
                   timeREM: Int = 0,
                   isWorkingOutWatch : Boolean = false,
                   isWorkingOutBpm: Boolean = false,
                   caloriesConsumedBpm: Int = 0,
                   stepsLast4Minutes: Int = 0,
                   stepsLast8Minutes: Int = 0,
                   timestampLastSteps: Long = 0,
                   isWalking : Boolean = false,
                   timeStampStartWorkout: Long = 0,
                   maxBpmWorkout: Int = 0,
                   minBpmWorkout: Int = 0
                   ) : State {
    return State(isSleeping = isSleeping,
        sleepStage = sleepStage,
        sleepCycle = sleepCycle,
        timeLightSleep = timeLightSleep,
        timeDeepSleep = timeDeepSleep,
        timeREM = timeREM,
        isWorkingOutWatch = isWorkingOutWatch,
        isWorkingOutBpm = isWorkingOutBpm,
        caloriesConsumedBpm = caloriesConsumedBpm,
        stepsLast8Minutes = stepsLast8Minutes,
        stepsLast4Minutes = stepsLast4Minutes,
        timestampLastSteps = timestampLastSteps,
        isWalking = isWalking,
        timestampStartWorkout = timeStampStartWorkout,
        maxBpmWorkout = maxBpmWorkout,
        minBpmWorkout = minBpmWorkout
        )
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
