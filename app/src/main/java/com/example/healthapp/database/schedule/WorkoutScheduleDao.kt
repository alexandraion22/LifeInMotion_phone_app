package com.example.healthapp.database.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WorkoutScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutSchedule: WorkoutSchedule)

    @Query("SELECT * FROM workout_schedule_table WHERE id = :day")
    suspend fun getListForDay(day: Int): WorkoutSchedule?

    @Query("DELETE FROM workout_schedule_table WHERE id == :day")
    suspend fun deleteListForDay(day: Int)

    @Query("DELETE FROM workout_schedule_table")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteVideoFromDay(day: Int, videoId: String) {
        val workoutSchedule = getListForDay(day)
        workoutSchedule?.let {
            val updatedWorkouts = it.workouts.toMutableSet()
            updatedWorkouts.remove(videoId)
            insert(it.copy(workouts = updatedWorkouts))
        }
    }

    @Transaction
    suspend fun addVideoToDay(day: Int, videoId: String) {
        val workoutSchedule = getListForDay(day)
        workoutSchedule?.let {
            val updatedWorkouts = it.workouts.toMutableSet()
            if (!updatedWorkouts.contains(videoId)) {
                updatedWorkouts.add(videoId)
                insert(it.copy(workouts = updatedWorkouts))
            }
        } ?: run {
            insert(WorkoutSchedule(id = day, workouts = setOf(videoId)))
        }
    }
}
