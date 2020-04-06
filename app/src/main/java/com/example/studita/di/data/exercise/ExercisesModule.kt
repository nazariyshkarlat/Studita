package com.example.studita.di.data.exercise

import com.example.studita.data.entity.mapper.exercise.ExercisesDataMapper
import com.example.studita.data.net.ExercisesService
import com.example.studita.data.repository.exercise.ExercisesRepositoryImpl
import com.example.studita.data.repository.datasource.exercises.CloudExercisesDataStore
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.exercises.ExercisesInteractor
import com.example.studita.domain.interactor.exercises.ExercisesInteractorImpl
import com.example.studita.domain.repository.ExercisesRepository

object ExercisesModule {

    private lateinit var config: DI.Config

    private var repository: ExercisesRepository? = null
    private var exercisesInteractor: ExercisesInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getExercisesInteractorImpl(): ExercisesInteractor {
        if (config == DI.Config.RELEASE && exercisesInteractor == null)
            exercisesInteractor =
                makeExercisesInteractor(
                    getExercisesRepository()
                )
        return exercisesInteractor!!
    }

    private fun getExercisesRepository(): ExercisesRepository {
        if (repository == null)
            repository =
                ExercisesRepositoryImpl(
                    getExercisesDataStoreFactory(),
                    ExercisesDataMapper()
                )
        return repository!!
    }

    private fun makeExercisesInteractor(repository: ExercisesRepository) =
       ExercisesInteractorImpl(
            repository
        )

    private fun getExercisesDataStore() =
        CloudExercisesDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(ExercisesService::class.java)
        )

    private fun getExercisesDataStoreFactory() =
        ExercisesDataStoreFactoryImpl(
            getExercisesDataStore()
        )
}