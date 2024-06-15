package com.example.healthapp.database.workouts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthapp.database.bpm.daily.BpmDaily

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Query("SELECT * FROM workouts_table WHERE timestamp >= :startOfDay AND timestamp <:endOfDay")
    suspend fun getEntriesForDay(startOfDay: Long, endOfDay:Long): List<Workout>

    @Query("SELECT * FROM workouts_table WHERE timestamp >= :startOfWeek AND timestamp < :startOfNextWeek")
    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<Workout>

    @Query("SELECT * FROM workouts_table WHERE timestamp >= :startOfMonth AND timestamp < :startOfNextMonth")
    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<Workout>
    @Query("DELETE FROM workouts_table")
    suspend fun deleteAll()

    @Query("UPDATE workouts_table SET timestamp = :timestamp, duration = :duration, type = :type, calories = :calories, minHR = :minHR, maxHR = :maxHR, meanHR = :meanHR, confirmed = :confirmed WHERE id = :id")
    suspend fun updateWorkoutById(id: Int, timestamp: Long, duration: Long, type: String, calories: Int, minHR: Int, maxHR: Int, meanHR: Int, confirmed: Boolean)

    @Query("DELETE FROM workouts_table WHERE id = :id")
    suspend fun deleteWorkoutById(id: Int)
}