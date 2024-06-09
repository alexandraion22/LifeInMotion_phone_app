package com.example.healthapp.database.bpm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class BpmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BpmRepository
    val _bpms = MutableStateFlow<List<Bpm>>(emptyList())
    val bpms: StateFlow<List<Bpm>> = _bpms

    init {
        val bpmDao = BpmDatabase.getDatabase(application).bpmDao()
        repository = BpmRepository(bpmDao)
    }

    fun insert(bpm: Bpm) = viewModelScope.launch {
        repository.insert(bpm)
    }

    fun deleteAllBpms() = viewModelScope.launch {
        repository.deleteAllBpms()
    }

    suspend fun getAllPastHour(): List<Bpm> {
        return repository.getAllPastHour()
    }

}
