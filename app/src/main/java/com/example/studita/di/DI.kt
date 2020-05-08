package com.example.studita.di

import android.app.Application
import com.example.studita.di.data.*
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.di.data.exercise.ExercisesModule

object DI{


    sealed class Config {
        object RELEASE : Config()
        object TEST : Config()
    }

    fun initialize(app: Application, configuration: Config = DI.Config.RELEASE) {
        NetworkModule.initialize(app)
        DatabaseModule.initialize(app)
        CacheModule.initialize(app)
        LevelsModule.initialize(configuration)
        ChapterModule.initialize(configuration)
        AuthorizationModule.initialize(configuration)
        ExercisesModule.initialize(configuration)
        ExerciseResultModule.initialize(configuration)
        UserDataModule.initialize(configuration)
        UserStatisticsModule.initialize(configuration)
        InterestingModule.initialize(configuration)
        SubscribeEmailModule.initialize(configuration)
        CompleteExercisesModule.initialize(configuration)
    }

}