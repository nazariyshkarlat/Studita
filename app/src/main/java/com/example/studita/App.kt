package com.example.studita

import android.app.Application
import com.example.studita.di.DI
import com.example.studita.di.data.ChapterModule
import com.example.studita.di.data.InterestingModule
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.exercise.ExercisesModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        GlobalScope.launch {
            LevelsModule.getLevelsInteractorImpl().downloadLevels()
            ChapterModule.getChapterInteractorImpl().downloadChapters()
            ExercisesModule.getExercisesInteractorImpl().downloadOfflineExercises()
        }
    }

}