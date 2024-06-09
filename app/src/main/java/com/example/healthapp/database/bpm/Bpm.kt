package com.example.healthapp.database.bpm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "bpm_table")
data class Bpm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val bpm: Int
)