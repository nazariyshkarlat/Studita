package com.example.studita.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studita.data.database.user_data.UserDataCompletedPartsTypeConverter
import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity

@TypeConverters(UserDataCompletedPartsTypeConverter::class)
@Database(
    entities = [UserDataEntity::class, UserStatisticsEntity::class],
    version = StuditaDatabase.DB_VERSION
)
abstract class StuditaDatabase : RoomDatabase() {

    abstract fun getUserDataDao(): UserDataDao
    abstract fun getUserStatisticsDao(): UserStatisticsDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "studita_database"
    }
}