package com.example.studita.di.data.exercise

import com.example.studita.data.net.ExerciseResultService
import com.example.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreImpl
import com.example.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl
import com.example.studita.data.repository.exercise.ExerciseResultRepositoryImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.exercises.ExerciseResultInteractor
import com.example.studita.domain.interactor.exercises.ExerciseResultInteractorImpl
import com.example.studita.domain.repository.ExerciseResultRepository
import com.example.studita.service.SyncExercisesReportsImpl


object ExerciseResultModule {

    private lateinit var config: DI.Config

    private var repository: ExerciseResultRepository? = null
    private var exerciseResultInteractor: ExerciseResultInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getExerciseResultInteractorImpl(): ExerciseResultInteractor {
        if (config == DI.Config.RELEASE && exerciseResultInteractor == null)
            exerciseResultInteractor =
                makeExerciseResultInteractor(
                    getExerciseResultRepository()
                )
        return exerciseResultInteractor!!
    }

    private fun getExerciseResultRepository(): ExerciseResultRepository {
        if (repository == null)
            repository =
                ExerciseResultRepositoryImpl(
                    getExercisesDataStoreFactory()
                )
        return repository!!
    }

    private fun makeExerciseResultInteractor(repository: ExerciseResultRepository) =
        ExerciseResultInteractorImpl(
            repository,
            SyncExercisesReportsImpl()
        )

    private fun getExerciseResultDataStore() =
        ExerciseResultDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(ExerciseResultService::class.java)
        )

    private fun getExercisesDataStoreFactory() =
        ExerciseResultDataStoreFactoryImpl(
            getExerciseResultDataStore()
        )
}