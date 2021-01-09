package com.studita.di.data.exercise

import com.studita.data.cache.exercises.ExercisesCache
import com.studita.data.cache.exercises.ExercisesCacheImpl
import com.studita.data.net.ExercisesService
import com.studita.data.net.OfflineExercisesService
import com.studita.data.repository.datasource.exercises.*
import com.studita.data.repository.exercise.ExercisesRepositoryImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.exercises.ExercisesInteractor
import com.studita.domain.interactor.exercises.ExercisesInteractorImpl
import com.studita.domain.repository.ExercisesRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createExercisesModule(config: DI.Config) = configModule(configuration = config) {

    single {
        ExercisesInteractorImpl(
            get()
        )
    } bind (ExercisesInteractor::class)

    single {
        ExercisesRepositoryImpl(
            get()
        )
    } bind (ExercisesRepository::class)

    single {
        ExercisesDataStoreFactoryImpl(
            get(),
            get()
        )
    } bind (ExercisesDataStoreFactory::class)

    single {
        CloudExercisesJsonDataStore(
            GlobalContext.get().get(),
            getService(ExercisesService::class.java),
            getService(OfflineExercisesService::class.java),
        )
    } bind (ExercisesJsonDataStore::class)

    single {
        DiskExercisesJsonDataStore(
            get()
        )
    }

    single{
        ExercisesCacheImpl(GlobalContext.get().get())
    } bind (ExercisesCache::class)
}