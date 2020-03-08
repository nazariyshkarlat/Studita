package com.example.studita.di

import android.app.Application
import com.example.studita.di.exercise.ExerciseResultModule
import com.example.studita.di.exercise.ExercisesModule
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.presentation.model.mapper.ExercisesUiModelMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DI{


    sealed class Config {
        object RELEASE : Config()
        object TEST : Config()
    }

    fun initialize(app: Application, configuration: Config = DI.Config.RELEASE) {
        NetworkModule.initialize(app)
        LevelsModule.initialize(configuration)
        ChapterPartsModule.initialize(configuration)
        AuthorizationModule.initialize(configuration)
        ExercisesModule.initialize(configuration)
        ExerciseResultModule.initialize(configuration)
    }

}