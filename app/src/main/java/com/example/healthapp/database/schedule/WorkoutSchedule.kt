package com.example.healthapp.database.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_schedule_table")
data class WorkoutSchedule(
    @PrimaryKey val id: Int,
    val workouts: Set<String>
)