package com.example.healthapp.database.bpm
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthapp.database.users.UserViewModel

class BpmViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BpmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BpmViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}