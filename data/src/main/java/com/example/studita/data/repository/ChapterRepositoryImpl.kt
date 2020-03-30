package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.ChapterDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.chapter.ChapterDataStore
import com.example.studita.data.repository.datasource.chapter.ChapterJsonDataStoreFactory
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.repository.ChapterRepository

class ChapterRepositoryImpl(private val chapterJsonDataStoreFactory: ChapterJsonDataStoreFactory,
                            private val chapterDataMapper: ChapterDataMapper,
                            private val connectionManager: ConnectionManager): ChapterRepository {

    override suspend fun getChapter(chapterNumber: Int): Pair<Int, ChapterData> =
        with(ChapterDataStore(chapterJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) ChapterJsonDataStoreFactory.Priority.CACHE else ChapterJsonDataStoreFactory.Priority.CLOUD)).getChapterEntity(chapterNumber)){
             this.first to chapterDataMapper.map(this.second)
        }

}