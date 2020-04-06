package com.example.studita

import android.app.Application
import com.example.studita.di.DI
import com.example.studita.di.data.InterestingModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
    }

}