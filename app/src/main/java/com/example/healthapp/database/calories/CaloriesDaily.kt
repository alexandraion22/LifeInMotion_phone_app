package com.example.healthapp.database.calories

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calories_daily_table")
data class CaloriesDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val totalCalories: Int
)