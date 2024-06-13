package com.example.healthapp.database.schedule

import javax.inject.Inject

class WorkoutScheduleRepository @Inject constructor(private val workoutScheduleDao: WorkoutScheduleDao) {

    suspend fun insert(workoutSchedule: WorkoutSchedule) {
        workoutScheduleDao.insert(workoutSchedule)
    }

   suspend fun getListForDay(day: Int): WorkoutSchedule? {
       return workoutScheduleDao.getListForDay(day)
   }

    suspend fun deleteListForDay(day: Int) {
        workoutScheduleDao.deleteListForDay(day)
    }

    suspend fun deleteAll() {
        workoutScheduleDao.deleteAll()
    }

    suspend fun deleteVideoFromDay(day: Int, videoId: String) {
        workoutScheduleDao.deleteVideoFromDay(day, videoId)
    }

    suspend fun addVideoToDay(day: Int, videoId: String) {
        workoutScheduleDao.addVideoToDay(day, videoId)
    }
}
