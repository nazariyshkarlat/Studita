package com.example.studita.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.studita.data.database.StuditaDatabase

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