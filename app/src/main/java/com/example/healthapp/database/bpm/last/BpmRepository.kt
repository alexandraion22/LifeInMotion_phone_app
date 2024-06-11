package com.example.healthapp.database.bpm.last

import javax.inject.Inject

class BpmRepository @Inject constructor(private val bpmDao: BpmDao) {
    suspend fun insert(bpm: Bpm) {
        bpmDao.insert(bpm)
    }

    suspend fun deleteAllBpms() {
        bpmDao.deleteAll()
    }

    suspend fun getFirst(): Bpm {
        return bpmDao.getFirst()
    }
}