package com.example.studita.di

import android.app.Application
import com.example.studita.di.data.*
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.di.data.exercise.ExercisesModule
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.user_statistics.UserStatisticsInteractorImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DI{


    sealed class Config {
        object RELEASE : Config()
        object TEST : Config()
    }

    fun initialize(app: Application, configuration: Config = DI.Config.RELEASE) {
        NetworkModule.initialize(app)
        DatabaseModule.initialize(app)
        DiskModule.initialize(app)
        LevelsModule.initialize(configuration)
        ChapterModule.initialize(configuration)
        AuthorizationModule.initialize(configuration)
        ExercisesModule.initialize(configuration)
        ExerciseResultModule.initialize(configuration)
        UserDataModule.initialize(configuration)
        UserStatisticsModule.initialize(configuration)
        InterestingModule.initialize(configuration)
        SubscribeEmailModule.initialize(configuration)
        ObtainedExerciseDataModule.initialize(configuration)
    }

}