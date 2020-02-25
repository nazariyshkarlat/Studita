package com.example.studita.di

import com.example.studita.data.entity.mapper.ChapterPartDataMapper
import com.example.studita.data.net.ChapterPartsService
import com.example.studita.data.repository.ChapterPartsRepositoryImpl
import com.example.studita.data.repository.datasource.chapter_parts.ChapterPartsDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.chapter_parts.CloudChapterPartsDataStore
import com.example.studita.data.repository.datasource.exercises.CloudExercisesDataStore
import com.example.studita.domain.interactor.chapter_parts.ChapterPartsInteractor
import com.example.studita.domain.interactor.chapter_parts.ChapterPartsInteractorImpl
import com.example.studita.domain.repository.ChapterPartsRepository

object ChapterPartsModule {

    private lateinit var config: DI.Config

    private var repository: ChapterPartsRepository? = null
    private var chapterPartsInteractor: ChapterPartsInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getChapterPartsInteractorImpl(): ChapterPartsInteractor {
        if (config == DI.Config.RELEASE && chapterPartsInteractor == null)
            chapterPartsInteractor = makeChapterPartsInteractor(getChapterPartsRepository())
        return chapterPartsInteractor!!
    }

    private fun getChapterPartsRepository(): ChapterPartsRepository {
        if (repository == null)
            repository = ChapterPartsRepositoryImpl(getChapterPartsDataStoreFactory(), ChapterPartDataMapper())
        return repository!!
    }

    private fun makeChapterPartsInteractor(repository: ChapterPartsRepository) =
        ChapterPartsInteractorImpl(
            repository
        )

    private fun getCloudChapterPartsDataStore() =
        CloudChapterPartsDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(ChapterPartsService::class.java)
        )

    private fun getChapterPartsDataStoreFactory() =
        ChapterPartsDataStoreFactoryImpl(
            getCloudChapterPartsDataStore()
        )
}