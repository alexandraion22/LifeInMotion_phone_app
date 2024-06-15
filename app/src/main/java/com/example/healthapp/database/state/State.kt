package com.example.healthapp.database.state

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "state_table")
data class State(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isSleeping: Boolean,
    val isWorkingOutWatch: Boolean,
    val isWorkingOutBpm: Boolean,
    val stepsLast8Minutes: Int,
    val stepsLast4Minutes: Int,
    val timestampLastSteps: Long,
    val caloriesConsumedBpm: Int
)