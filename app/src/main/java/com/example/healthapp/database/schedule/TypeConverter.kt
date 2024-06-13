package com.example.healthapp.database.schedule

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): Set<String> {
        val setType = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(value, setType)
    }

    @TypeConverter
    fun fromSet(set: Set<String>): String {
        return Gson().toJson(set)
    }
}
