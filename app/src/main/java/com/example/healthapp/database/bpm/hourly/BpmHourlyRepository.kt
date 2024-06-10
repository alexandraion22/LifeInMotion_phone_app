package com.example.healthapp.database.bpm.hourly

import javax.inject.Inject

class BpmHourlyRepository @Inject constructor(private val bpmHourlyDao: BpmHourlyDao) {
    suspend fun insert(bpmHourly: BpmHourly) {
        bpmHourlyDao.insert(bpmHourly)
    }

    suspend fun getEntryForHour(startOfHour: Long): BpmHourly? {
        return bpmHourlyDao.getEntryForHour(startOfHour)
    }

    suspend fun update(updatedEntry: BpmHourly) {
        bpmHourlyDao.deleteEntryForHour(updatedEntry.timestamp)
        bpmHourlyDao.insert(updatedEntry)
    }

    suspend fun getAllPastDay(startOfDay: Long, startOfNextDay: Long): List<BpmHourly> {
        return bpmHourlyDao.getAllPastDay(startOfDay,startOfNextDay)
    }
}