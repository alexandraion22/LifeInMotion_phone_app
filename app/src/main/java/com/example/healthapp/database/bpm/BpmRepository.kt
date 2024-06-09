package com.example.healthapp.database.bpm

import javax.inject.Inject

class BpmRepository @Inject constructor(private val bpmDao: BpmDao) {
    suspend fun insert(bpm: Bpm) {
        bpmDao.insert(bpm)
    }

    suspend fun deleteAllBpms() {
        bpmDao.deleteAll()
    }

    suspend fun getAllPastHour(startOfHourEpoch: Long,startOfNextHourEpoch: Long) : List<Bpm>{
        return bpmDao.getAllPastHour(startOfHourEpoch,startOfNextHourEpoch)
    }
}