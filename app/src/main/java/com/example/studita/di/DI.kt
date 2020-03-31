package com.example.studita.di

import android.app.Application
import com.example.studita.di.exercise.ExerciseResultModule
import com.example.studita.di.exercise.ExercisesModule
import com.example.studita.presentation.utils.PrefsUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DI{


    sealed class Config {
        object RELEASE : Config()
        object TEST : Config()
    }

    fun initialize(app: Application, configuration: Config = DI.Config.RELEASE) {
        NetworkModule.initialize(app)
        DiskModule.initialize(app)
        LevelsModule.initialize(configuration)
        ChapterModule.initialize(configuration)
        AuthorizationModule.initialize(configuration)
        ExercisesModule.initialize(configuration)
        ExerciseResultModule.initialize(configuration)
        UserDataModule.initialize(configuration)
    }

}