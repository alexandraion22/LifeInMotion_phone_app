package com.example.healthapp.database.goals

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals_table")
data class Goals(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val activityGoal: Int,
    val stepsGoal: Int,
    val caloriesGoal: Int
)