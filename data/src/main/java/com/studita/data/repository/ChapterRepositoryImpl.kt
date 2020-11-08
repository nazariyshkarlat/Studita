package com.studita.data.repository

import com.studita.data.entity.toRawEntity
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.repository.datasource.chapter.ChapterDataStoreImpl
import com.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactory
import com.studita.data.repository.datasource.chapter.CloudChapterJsonDataStore
import com.studita.data.repository.datasource.chapter.DiskChapterJsonDataStore
import com.studita.domain.entity.ChapterData
import com.studita.domain.repository.ChapterRepository

class ChapterRepositoryImpl(
    private val chapterJsonDataStoreFactory: ChapterJsonDataStoreFactory,
    private val connectionManager: ConnectionManager
) : ChapterRepository {

    override suspend fun getChapter(
        chapterNumber: Int,
        offlineMode: Boolean
    ): Pair<Int, ChapterData> =
        with(
            ChapterDataStoreImpl(chapterJsonDataStoreFactory.create(if (offlineMode) ChapterJsonDataStoreFactory.Priority.CACHE else ChapterJsonDataStoreFactory.Priority.CLOUD)).getChapterEntity(
                chapterNumber
            )
        ) {
            this.first to this.second.toRawEntity()
        }

    override suspend fun downloadChapters(): Int {
        val diskDataStore =
            (chapterJsonDataStoreFactory.create(ChapterJsonDataStoreFactory.Priority.CACHE) as DiskChapterJsonDataStore)
        return if (!diskDataStore.chaptersAreCached()) {
            val json =
                (chapterJsonDataStoreFactory.create(ChapterJsonDataStoreFactory.Priority.CLOUD) as CloudChapterJsonDataStore).getAllChaptersJson()
            diskDataStore.saveChaptersJson(json)
            200
        } else
            409
    }

}