package com.example.healthapp.database.steps.hourly

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps_hourly_table")
data class StepsHourly(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val steps: Int
)