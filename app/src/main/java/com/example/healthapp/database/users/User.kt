package com.example.healthapp.database.users
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val fullName: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val gender: String,
    val activityLevel: Int,
    val bmi: Double
)