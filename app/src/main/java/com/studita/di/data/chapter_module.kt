package com.studita.di.data

import com.studita.data.cache.chapter.ChaptersCache
import com.studita.data.cache.chapter.ChaptersCacheImpl
import com.studita.data.net.ChapterService
import com.studita.data.net.ChaptersService
import com.studita.data.repository.ChapterRepositoryImpl
import com.studita.data.repository.datasource.chapter.*
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.chapter.ChapterInteractor
import com.studita.domain.interactor.chapter.ChapterInteractorImpl
import com.studita.domain.repository.ChapterRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createChapterModule(config: DI.Config) = configModule(configuration = config) {

    single{
        ChapterInteractorImpl(
            get()
        )
    } bind (ChapterInteractor::class)

    single{
        ChapterRepositoryImpl(
                get(),
                GlobalContext.get().get()
            )
    } bind (ChapterRepository::class)

    single {
        CloudChapterJsonDataStore(
            GlobalContext.get().get(),
            getService(ChapterService::class.java),
            getService(ChaptersService::class.java)
        )
    }

    single {
        DiskChapterJsonDataStore(get())
    }

    single {
        ChaptersCacheImpl(GlobalContext.get().get())
    } bind (ChaptersCache::class)

    single {
        ChapterJsonDataStoreFactoryImpl(
            get(),
            get()
        )
    } bind (ChapterJsonDataStoreFactory::class)
}