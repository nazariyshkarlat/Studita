package com.studita.di

import android.app.Application
import com.studita.di.data.*
import com.studita.di.data.exercise.createCompleteExercisesModule
import com.studita.di.data.exercise.createExerciseReportModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

object DI {


    sealed class Config {
        object RELEASE : Config()
        object TEST : Config()
    }

    fun initialize(application: Application, configuration: Config = DI.Config.RELEASE) {
        startKoin{
            androidLogger()
            androidContext(application)
            modules(networkModule, cacheModule, databaseModule,
                createLevelsModule(configuration),
                createChapterModule(configuration),
                createAuthorizationModule(configuration),
                createCompleteExercisesModule(configuration),
                createExerciseReportModule(configuration),
                createUserDataModule(configuration),
                createUserStatisticsModule(configuration),
                createSubscribeEmailModule(configuration),
                createEditProfileModule(configuration),
                createPrivacySettingsModule(configuration),
                createUsersModule(configuration),
                createNotificationsModule(configuration),
                createOfflineDataModule(configuration),
                createAchievementsModule(configuration),
                achievementsViewModel,
                selectCourseFragmentViewModel
            )
        }
    }

}