package com.example.healthapp.database.workouts

import com.example.healthapp.database.bpm.daily.BpmDaily
import javax.inject.Inject

class WorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) {
    suspend fun insert(workout: Workout) {
        workoutDao.insert(workout)
    }

    suspend fun deleteAllWorkouts() {
        workoutDao.deleteAll()
    }

    suspend fun getEntriesForDay(startOfDay: Long, endOfDay: Long): List<Workout>{
        return workoutDao.getEntriesForDay(startOfDay, endOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<Workout> {
        return workoutDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<Workout> {
        return workoutDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(id: Int) {
        workoutDao.deleteWorkoutById(id)
    }

    suspend fun update(id: Int, updatedEntry: Workout) {
        workoutDao.updateWorkoutById(id, updatedEntry.timestamp, updatedEntry.duration, updatedEntry.type, updatedEntry.calories, updatedEntry.minHR, updatedEntry.maxHR, updatedEntry.meanHR, updatedEntry.confirmed)
    }
}