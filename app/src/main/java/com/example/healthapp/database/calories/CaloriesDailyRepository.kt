package com.example.healthapp.database.calories

import javax.inject.Inject

class CaloriesDailyRepository @Inject constructor(private val caloriesDailyDao: CaloriesDailyDao) {
    suspend fun insert(bpmDaily: CaloriesDaily) {
        caloriesDailyDao.insert(bpmDaily)
    }

    suspend fun deleteAll() {
        caloriesDailyDao.deleteAll()
    }


    suspend fun getEntryForDay(startOfDay: Long): CaloriesDaily? {
        return caloriesDailyDao.getEntryForDay(startOfDay)
    }

    suspend fun getAllPast7days(startOfWeek: Long, startOfNextWeek: Long): List<CaloriesDaily> {
        return caloriesDailyDao.getAllPast7days(startOfWeek,startOfNextWeek)
    }

    suspend fun getAllPast31days(startOfMonth: Long, startOfNextMonth: Long): List<CaloriesDaily> {
        return caloriesDailyDao.getAllPast31days(startOfMonth, startOfNextMonth)
    }

    suspend fun deleteEntryForDay(startOfDay: Long) {
        caloriesDailyDao.deleteEntryForDay(startOfDay)
    }

    suspend fun update(updatedEntry: CaloriesDaily) {
        if(caloriesDailyDao.getEntryForDay(updatedEntry.timestamp)!=null)
            caloriesDailyDao.deleteEntryForDay(updatedEntry.timestamp)
        caloriesDailyDao.insert(updatedEntry)
    }
}