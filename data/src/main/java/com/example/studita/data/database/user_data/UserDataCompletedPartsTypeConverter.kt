package com.example.studita.data.database.user_data

import androidx.room.TypeConverter
import com.google.gson.Gson

class UserDataCompletedPartsTypeConverter {

    @TypeConverter
    fun listToJson(value: List<Int>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Int>::class.java).toList()
}