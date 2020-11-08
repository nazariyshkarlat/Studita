package com.studita.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.studita.data.database.StuditaDatabase

object DatabaseModule {

    lateinit var studitaDatabase: StuditaDatabase

    fun initialize(app: Application) {
        studitaDatabase = Room.databaseBuilder(
            app.applicationContext,
            StuditaDatabase::class.java,
            StuditaDatabase.DB_NAME
        ).build()
    }
}