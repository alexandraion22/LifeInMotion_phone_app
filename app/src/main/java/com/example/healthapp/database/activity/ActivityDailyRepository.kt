package com.example.healthapp.database.activity

import javax.inject.Inject

class ActivityDailyRepository @Inject constructor(private val activityDailyDao: ActivityDailyDao) {
    suspend fun insert(activityDaily: ActivityDaily) {
        activityDailyDao.insert(activityDaily)
    }

    suspend fun deleteAll() {
        activityDailyDao.deleteAll()
    }


    suspend fun getEntryForDay(startOfDay: Long): ActivityDaily? {
        return activityDailyDao.getEntryForDay(startOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<ActivityDaily> {
        return activityDailyDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<ActivityDaily> {
        return activityDailyDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(startOfDay: Long) {
        activityDailyDao.deleteEntryForDay(startOfDay)
    }

    suspend fun update(updatedEntry: ActivityDaily) {
        activityDailyDao.deleteEntryForDay(updatedEntry.timestamp)
        activityDailyDao.insert(updatedEntry)
    }
}