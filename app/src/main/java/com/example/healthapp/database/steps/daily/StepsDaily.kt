package com.example.healthapp.database.steps.daily

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps_daily_table")
data class StepsDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val steps : Int
)