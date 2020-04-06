package com.example.studita.data.repository.datasource.chapter

class ChapterJsonDataStoreFactoryImpl(
    private val cloudChapterJsonDataStore: CloudChapterJsonDataStore,
    private val diskChapterJsonDataStore: DiskChapterJsonDataStore
) : ChapterJsonDataStoreFactory{

    override fun create(priority: ChapterJsonDataStoreFactory.Priority) =
        if (priority == ChapterJsonDataStoreFactory.Priority.CLOUD)
            cloudChapterJsonDataStore
        else
            diskChapterJsonDataStore
}

interface ChapterJsonDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): ChapterJsonDataStore
}