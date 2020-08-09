package com.example.studita.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object CacheModule {

    lateinit var sharedPreferences: SharedPreferences
    private const val PREFS_NAME = "studita_cache"

    fun initialize(app: Application) {
        sharedPreferences = app.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
    }
}