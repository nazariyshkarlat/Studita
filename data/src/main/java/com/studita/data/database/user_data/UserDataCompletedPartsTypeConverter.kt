package com.studita.data.database.user_data

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserDataCompletedPartsTypeConverter {

    @TypeConverter
    fun listToJson(value: List<Int>) =Json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String) = Json.decodeFromString<List<Int>>(value).toList()
}