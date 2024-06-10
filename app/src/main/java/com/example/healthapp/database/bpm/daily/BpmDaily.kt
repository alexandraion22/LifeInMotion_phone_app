package com.example.healthapp.database.bpm.daily

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bpm_daily_table")
data class BpmDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val maxBpm: Int,
    val minBpm: Int
)