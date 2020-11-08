package com.studita.di.data

import com.studita.data.cache.chapter.ChaptersCacheImpl
import com.studita.data.net.ChapterService
import com.studita.data.net.ChaptersService
import com.studita.data.repository.ChapterRepositoryImpl
import com.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactoryImpl
import com.studita.data.repository.datasource.chapter.CloudChapterJsonDataStore
import com.studita.data.repository.datasource.chapter.DiskChapterJsonDataStore
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.chapter.ChapterInteractor
import com.studita.domain.interactor.chapter.ChapterInteractorImpl
import com.studita.domain.repository.ChapterRepository

object ChapterModule {

    private lateinit var config: DI.Config

    private var repository: ChapterRepository? = null
    private var chapterInteractor: ChapterInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getChapterInteractorImpl(): ChapterInteractor {
        if (config == DI.Config.RELEASE && chapterInteractor == null)
            chapterInteractor =
                makeChapterInteractor(
                    getChapterRepository()
                )
        return chapterInteractor!!
    }

    private fun getChapterRepository(): ChapterRepository {
        if (repository == null)
            repository = ChapterRepositoryImpl(
                getChapterJsonDataStoreFactory(),
                NetworkModule.connectionManager
            )
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
            NetworkModule.getService(ChaptersService::class.java)
        )

    private fun getDiskChapterDataStore() =
        DiskChapterJsonDataStore(getChapterCacheImpl())

    private fun getChapterCacheImpl() =
        ChaptersCacheImpl(CacheModule.sharedPreferences)

    private fun getChapterJsonDataStoreFactory() =
        ChapterJsonDataStoreFactoryImpl(
            getCloudChapterDataStore(),
            getDiskChapterDataStore()
        )
}