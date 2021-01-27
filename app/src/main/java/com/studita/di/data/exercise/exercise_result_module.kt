package com.studita.di.data.exercise

import com.studita.data.net.ChapterService
import com.studita.data.net.ExerciseResultService
import com.studita.data.repository.datasource.exercises.ExercisesDataStore
import com.studita.data.repository.datasource.exercises.ExercisesDataStoreFactory
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStore
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactory
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactoryImpl
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreImpl
import com.studita.data.repository.exercise.ExerciseResultRepositoryImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.exercises.ExerciseResultInteractor
import com.studita.domain.interactor.exercises.ExerciseResultInteractorImpl
import com.studita.domain.repository.ExerciseResultRepository
import com.studita.domain.repository.ExercisesRepository
import com.studita.service.SyncExercisesReportsImpl
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind


fun createExerciseResultModule(config: DI.Config) =  configModule(configuration = config){

    single {
        ExerciseResultInteractorImpl(
            get(),
            SyncExercisesReportsImpl()
        )
    } bind (ExerciseResultInteractor::class)

    single{
        ExerciseResultRepositoryImpl(
            get()
        )
    } bind (ExerciseResultRepository::class)

    single{
        ExerciseResultDataStoreImpl(
            GlobalContext.get().get(),
            getService( ExerciseResultService::class.java) ,
        )
    } bind (ExerciseResultDataStore::class)


single {
        ExerciseResultDataStoreFactoryImpl(
            get()
        )
    } bind (ExerciseResultDataStoreFactory::class)
}