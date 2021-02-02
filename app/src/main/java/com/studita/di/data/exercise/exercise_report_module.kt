package com.studita.di.data.exercise

import com.studita.data.net.ExerciseReportService
import com.studita.data.repository.datasource.exercises.ExerciseReportDataStore
import com.studita.data.repository.datasource.exercises.ExerciseReportDataStoreFactory
import com.studita.data.repository.datasource.exercises.ExerciseReportDataStoreFactoryImpl
import com.studita.data.repository.datasource.exercises.ExerciseReportDataStoreImpl
import com.studita.data.repository.exercise.ExerciseReportRepositoryImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.exercises.ExerciseReportnteractor
import com.studita.domain.interactor.exercises.ExerciseReportnteractorImpl
import com.studita.domain.repository.ExerciseReportRepository
import com.studita.service.SyncExercisesReportsImpl
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind


fun createExerciseReportModule(config: DI.Config) =  configModule(configuration = config){

    single {
        ExerciseReportnteractorImpl(
            get(),
            SyncExercisesReportsImpl()
        )
    } bind (ExerciseReportnteractor::class)

    single{
        ExerciseReportRepositoryImpl(
            get()
        )
    } bind (ExerciseReportRepository::class)

    single{
        ExerciseReportDataStoreImpl(
            GlobalContext.get().get(),
            getService( ExerciseReportService::class.java) ,
        )
    } bind (ExerciseReportDataStore::class)


single {
        ExerciseReportDataStoreFactoryImpl(
            get()
        )
    } bind (ExerciseReportDataStoreFactory::class)
}