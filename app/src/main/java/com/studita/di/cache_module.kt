package com.studita.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val PREFS_NAME = "studita_cache"

val cacheModule = module{
    single {
        providePrefs(androidApplication())
    }
}

private fun providePrefs(application: Application) =
    application.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )