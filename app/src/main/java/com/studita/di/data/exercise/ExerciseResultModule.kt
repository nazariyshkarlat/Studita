package com.studita.di.data.exercise

import com.studita.data.net.ExerciseResultService
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactoryImpl
import com.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactoryImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl
import com.studita.data.repository.exercise.ExerciseResultRepositoryImpl
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.exercises.ExerciseResultInteractor
import com.studita.domain.interactor.exercises.ExerciseResultInteractorImpl
import com.studita.domain.repository.ExerciseResultRepository
import com.studita.service.SyncExercisesReportsImpl


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
                    getExerciseResultDataStoreFactory()
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

    private fun getExerciseResultDataStoreFactory() =
        ExerciseResultDataStoreFactoryImpl(
            getExerciseResultDataStore()
        )
}