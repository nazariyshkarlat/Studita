package com.example.studita.data.repository

import com.example.studita.data.cache.chapter.ChapterCache
import com.example.studita.data.entity.mapper.ChapterDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.chapter.ChapterDataStoreImpl
import com.example.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactory
import com.example.studita.data.repository.datasource.chapter.CloudChapterJsonDataStore
import com.example.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.repository.ChapterRepository

class ChapterRepositoryImpl(private val chapterJsonDataStoreFactory: ChapterJsonDataStoreFactory,
                            private val chapterDataMapper: ChapterDataMapper,
                            private val connectionManager: ConnectionManager,
                            private val chapterCache: ChapterCache): ChapterRepository {

    override suspend fun getChapter(chapterNumber: Int): Pair<Int, ChapterData> =
        with(ChapterDataStoreImpl(chapterJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) ChapterJsonDataStoreFactory.Priority.CACHE else ChapterJsonDataStoreFactory.Priority.CLOUD)).getChapterEntity(chapterNumber)){
             this.first to chapterDataMapper.map(this.second)
        }

    override suspend fun downloadChapters(): Int =
        if(!chapterCache.isCached(1)){
            val json = (chapterJsonDataStoreFactory.create(ChapterJsonDataStoreFactory.Priority.CLOUD) as CloudChapterJsonDataStore).getAllChaptersJson()
            chapterCache.saveChaptersJson(json)
            200
        }else
            409

}