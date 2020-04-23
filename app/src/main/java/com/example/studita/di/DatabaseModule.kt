package com.example.studita.di

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studita.data.database.StuditaDatabase
import com.example.studita.data.database.user_data.UserDataCompletedPartsTypeConverter
import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity

object DatabaseModule {

    @Volatile
    var studitaDatabase: StuditaDatabase? = null

    private fun getInstance(context: Context): StuditaDatabase {
        val tempInstance =
            studitaDatabase
        if (tempInstance != null) {
            return tempInstance
        }

        synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                StuditaDatabase::class.java,
                StuditaDatabase.DB_NAME
            ).build()

            studitaDatabase = instance
            return instance
        }
    }

    fun initialize(app: Application) {
        getInstance(app)
    }
}