package com.example.studita

import android.app.Application
import com.example.studita.di.DI
import com.example.studita.di.LevelsModule

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
    }

}