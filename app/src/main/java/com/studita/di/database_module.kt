package com.studita.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.studita.data.database.StuditaDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        provideDatabase(androidContext())
    }
}

private fun provideDatabase(applicationContext: Context) =
    Room.databaseBuilder(
        applicationContext,
        StuditaDatabase::class.java,
        StuditaDatabase.DB_NAME
    ).addMigrations(StuditaDatabase.MIGRATION_1_2).build()