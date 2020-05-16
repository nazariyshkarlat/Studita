package com.example.studita.di.data

import com.example.studita.service.SyncCompletedExercisesImpl
import com.example.studita.data.entity.mapper.CompleteExercisesRequestMapper
import com.example.studita.data.entity.mapper.CompletedExercisesMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.CompleteExercisesService
import com.example.studita.data.repository.CompleteExercisesRepositoryImpl
import com.example.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.example.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractorImpl
import com.example.studita.domain.repository.CompleteExercisesRepository

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
                getCompleteExercisesDataStoreFactory(),
                getCompleteExercisesRequestMapper()
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
            NetworkModule.getService(CompleteExercisesService::class.java)
        )

    private fun getCompleteExercisesRequestMapper() =
        CompleteExercisesRequestMapper(UserIdTokenMapper(), CompletedExercisesMapper())

}
