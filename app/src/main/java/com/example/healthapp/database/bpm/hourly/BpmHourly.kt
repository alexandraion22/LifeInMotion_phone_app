package com.example.healthapp.database.bpm.hourly

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bpm_hourly_table")
data class BpmHourly(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val maxBpm: Int,
    val minBpm: Int
)