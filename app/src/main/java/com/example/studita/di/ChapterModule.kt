package com.example.studita.di

import com.example.studita.data.database.chapter_parts.ChapterCacheImpl
import com.example.studita.data.entity.mapper.ChapterDataMapper
import com.example.studita.data.net.ChapterService
import com.example.studita.data.repository.ChapterRepositoryImpl
import com.example.studita.data.repository.datasource.chapter.ChapterDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.chapter.CloudChapterJsonDataStore
import com.example.studita.data.repository.datasource.chapter.DiskChapterJsonDataStore
import com.example.studita.domain.interactor.chapter.ChapterInteractor
import com.example.studita.domain.interactor.chapter.ChapterInteractorImpl
import com.example.studita.domain.repository.ChapterRepository

object ChapterModule {

    private lateinit var config: DI.Config

    private var repository: ChapterRepository? = null
    private var chapterPartsInteractor: ChapterInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getChapterInteractorImpl(): ChapterInteractor {
        if (config == DI.Config.RELEASE && chapterPartsInteractor == null)
            chapterPartsInteractor = makeChapterInteractor(getChapterRepository())
        return chapterPartsInteractor!!
    }

    private fun getChapterRepository(): ChapterRepository {
        if (repository == null)
            repository = ChapterRepositoryImpl(getChapterDataStoreFactory(), ChapterDataMapper(), NetworkModule.connectionManager)
        return repository!!
    }

    private fun makeChapterInteractor(repository: ChapterRepository) =
        ChapterInteractorImpl(
            repository
        )

    private fun getCloudChapterDataStore() =
        CloudChapterJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(ChapterService::class.java),
            getChapterCacheImpl()
        )

    private fun getDiskChapterDataStore() =
        DiskChapterJsonDataStore(getChapterCacheImpl())

    private fun getChapterCacheImpl() =
        ChapterCacheImpl(DiskModule.sharedPreferences)

    private fun getChapterDataStoreFactory() =
        ChapterDataStoreFactoryImpl(
            getCloudChapterDataStore(),
            getDiskChapterDataStore()
        )
}