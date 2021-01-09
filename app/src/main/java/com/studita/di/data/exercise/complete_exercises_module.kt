package com.studita.di.data

import com.studita.data.database.StuditaDatabase
import com.studita.data.net.CompleteExercisesService
import com.studita.data.repository.CompleteExercisesRepositoryImpl
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStore
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactory
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactoryImpl
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractorImpl
import com.studita.domain.repository.CompleteExercisesRepository
import com.studita.service.SyncCompletedExercisesImpl
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createCompleteExercisesModule(config: DI.Config) = configModule(configuration = config) {

    single {
        CompleteExercisesInteractorImpl(
            get(),
            SyncCompletedExercisesImpl()
        )
    } bind (CompleteExercisesInteractor::class)

    single{
        CompleteExercisesRepositoryImpl(
                get()
            )
    } bind (CompleteExercisesRepository::class)

    single {
        CompleteExercisesDataStoreFactoryImpl(
            get()
        )
    } bind (CompleteExercisesDataStoreFactory::class)

    single {
        CompleteExercisesDataStoreImpl(
            GlobalContext.get().get(),
            getService(CompleteExercisesService::class.java),
            GlobalContext.get().get<StuditaDatabase>().getCompletedExercisesDao()
        )
    } bind (CompleteExercisesDataStore::class)

}
