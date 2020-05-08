package com.example.studita.di.data.exercise

import com.example.studita.data.cache.exercises.ExercisesCacheImpl
import com.example.studita.data.entity.mapper.exercise.ExercisesDataMapper
import com.example.studita.data.net.ExercisesService
import com.example.studita.data.net.OfflineExercisesService
import com.example.studita.data.repository.datasource.exercises.CloudExercisesJsonDataStore
import com.example.studita.data.repository.datasource.exercises.DiskExercisesJsonDataStore
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStore
import com.example.studita.data.repository.exercise.ExercisesRepositoryImpl
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreFactoryImpl
import com.example.studita.di.CacheModule
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

    private fun getExercisesDataStoreFactory() =
        ExercisesDataStoreFactoryImpl(
            getCloudExercisesJsonDataStore(),
            getDiskExercisesJsonDataStore()
        )

    private fun getCloudExercisesJsonDataStore() =
        CloudExercisesJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(ExercisesService::class.java),
            NetworkModule.getService(OfflineExercisesService::class.java)
        )

    private fun getDiskExercisesJsonDataStore() =
        DiskExercisesJsonDataStore(
            getExercisesCacheImpl()
        )

    private fun getExercisesCacheImpl() = ExercisesCacheImpl(CacheModule.sharedPreferences)
}