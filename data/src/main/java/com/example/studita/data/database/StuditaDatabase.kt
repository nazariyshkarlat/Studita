package com.example.studita.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studita.data.database.user_data.UserDataCompletedPartsTypeConverter
import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.entity.UserDataEntity

@TypeConverters(UserDataCompletedPartsTypeConverter::class)
@Database(
    entities = [UserDataEntity::class],
    version = StuditaDatabase.DB_VERSION
)
abstract class StuditaDatabase : RoomDatabase() {

    abstract fun getUserDataDao(): UserDataDao

    companion object {
        const val DB_VERSION = 4
        const val DB_NAME = "studita_database"
    }
}