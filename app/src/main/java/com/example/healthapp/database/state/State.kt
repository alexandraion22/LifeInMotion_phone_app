package com.example.healthapp.database.state

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "state_table")
data class State(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isSleeping: Boolean,
    val sleepStage: Int, // 0-awake, 1-light sleep, 2-deep sleep, 3-rem
    val sleepCycle: Int,
    val timeLightSleep: Int,
    val timeDeepSleep: Int,
    val timeREM: Int,
    val isWorkingOutWatch: Boolean,
    val isWorkingOutBpm: Boolean,
    val caloriesConsumedBpm: Int,
    val stepsLast8Minutes: Int,
    val stepsLast4Minutes: Int,
    val isWalking: Boolean,
    val timestampLastSteps: Long,
    val timestampStartWorkout: Long
)