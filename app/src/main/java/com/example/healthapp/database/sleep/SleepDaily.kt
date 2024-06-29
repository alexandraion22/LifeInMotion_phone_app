package com.example.healthapp.database.sleep

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_table")
data class SleepDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampStart: Long,
    val REMDuration: Int,
    val LightDuration: Int,
    val DeepDuration: Int,
    val givenScore: Int,
    val automaticScore: Int
)