package com.example.healthapp.database.workouts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts_table")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val duration: Long,
    val type: String,
    val calories: Int,
    val minHR: Int,
    val maxHR: Int,
    val meanHR: Int,
    val autoRecorder: Boolean,
    val confirmed: Boolean
)