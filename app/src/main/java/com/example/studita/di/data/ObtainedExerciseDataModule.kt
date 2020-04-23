package com.example.studita.di.data

import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.ObtainedExerciseDataService
import com.example.studita.data.repository.ObtainedExerciseDataRepositoryImpl
import com.example.studita.data.repository.datasource.obtained_exercise_data.CloudObtainedExerciseDataDataStore
import com.example.studita.data.repository.datasource.obtained_exercise_data.ObtainedExerciseDataDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.obtained_exercise_data.ObtainedExerciseDataInteractor
import com.example.studita.domain.interactor.obtained_exercise_data.ObtainedExerciseDataInteractorImpl
import com.example.studita.domain.repository.ObtainedExerciseDataRepository

object ObtainedExerciseDataModule {
    private lateinit var config: DI.Config

    private var repository: ObtainedExerciseDataRepository? = null
    private var obtainedExerciseDataInteractor: ObtainedExerciseDataInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getObtainedExerciseDataInteractorImpl(): ObtainedExerciseDataInteractor {
        if (config == DI.Config.RELEASE && obtainedExerciseDataInteractor == null)
            obtainedExerciseDataInteractor =
                makeObtainedExerciseDataInteractor(
                    getObtainedExerciseDataRepository()
                )
        return obtainedExerciseDataInteractor!!
    }

    private fun getObtainedExerciseDataRepository(): ObtainedExerciseDataRepository {
        if (repository == null)
            repository = ObtainedExerciseDataRepositoryImpl(
                getObtainedExerciseDataDataStoreFactory(),
                UserIdTokenMapper()
            )
        return repository!!
    }

    private fun makeObtainedExerciseDataInteractor(repository: ObtainedExerciseDataRepository) =
        ObtainedExerciseDataInteractorImpl(
            repository
        )

    private fun getCloudObtainedExerciseDataDataStore() =
        CloudObtainedExerciseDataDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(ObtainedExerciseDataService::class.java))

    private fun getObtainedExerciseDataDataStoreFactory() =
        ObtainedExerciseDataDataStoreFactoryImpl(
            getCloudObtainedExerciseDataDataStore()
        )

}