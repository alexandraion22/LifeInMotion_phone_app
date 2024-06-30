package com.example.healthapp.database.sleep

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_table")
data class SleepDaily(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampStart: Long,
    val REMDuration: Int,
    val lightDuration: Int,
    val deepDuration: Int,
    val givenScore: Int,
    val automaticScore: Int,
    val cycles: Int,
    val awakenings: Int
)