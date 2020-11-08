package com.studita.di.data.exercise

import com.studita.data.cache.exercises.ExercisesCacheImpl
import com.studita.data.net.ExercisesService
import com.studita.data.net.OfflineExercisesService
import com.studita.data.repository.datasource.exercises.CloudExercisesJsonDataStore
import com.studita.data.repository.datasource.exercises.DiskExercisesJsonDataStore
import com.studita.data.repository.datasource.exercises.ExercisesDataStoreFactoryImpl
import com.studita.data.repository.exercise.ExercisesRepositoryImpl
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.exercises.ExercisesInteractor
import com.studita.domain.interactor.exercises.ExercisesInteractorImpl
import com.studita.domain.repository.ExercisesRepository

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
                    getExercisesDataStoreFactory()
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