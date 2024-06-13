package com.example.healthapp.database.activity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_daily_table")
data class ActivityDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val activeTime: Int
)