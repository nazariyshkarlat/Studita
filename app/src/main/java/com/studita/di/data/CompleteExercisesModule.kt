package com.studita.di.data

import com.studita.data.database.completed_exercises.CompletedExercisesDao
import com.studita.data.net.CompleteExercisesService
import com.studita.data.repository.CompleteExercisesRepositoryImpl
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactoryImpl
import com.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreImpl
import com.studita.di.DI
import com.studita.di.DatabaseModule
import com.studita.di.NetworkModule
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractorImpl
import com.studita.domain.repository.CompleteExercisesRepository
import com.studita.service.SyncCompletedExercisesImpl

object CompleteExercisesModule {

    private lateinit var config: DI.Config

    private var repository: CompleteExercisesRepository? = null
    private var completeExercisesInteractor: CompleteExercisesInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getCompleteExercisesInteractorImpl(): CompleteExercisesInteractor {
        if (config == DI.Config.RELEASE && completeExercisesInteractor == null)
            completeExercisesInteractor =
                makeCompleteExercisesIntercator(
                    getCompleteExercisesRepository()
                )
        return completeExercisesInteractor!!
    }

    private fun getCompleteExercisesRepository(): CompleteExercisesRepository {
        if (repository == null)
            repository = CompleteExercisesRepositoryImpl(
                getCompleteExercisesDataStoreFactory()
            )
        return repository!!
    }

    private fun makeCompleteExercisesIntercator(repository: CompleteExercisesRepository) =
        CompleteExercisesInteractorImpl(
            repository,
            SyncCompletedExercisesImpl()
        )


    private fun getCompleteExercisesDataStoreFactory() =
        CompleteExercisesDataStoreFactoryImpl(
            getCompleteExercisesDataStore()
        )

    private fun getCompleteExercisesDataStore() =
        CompleteExercisesDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(CompleteExercisesService::class.java),
            DatabaseModule.studitaDatabase.getCompletedExercisesDao()
        )

}
