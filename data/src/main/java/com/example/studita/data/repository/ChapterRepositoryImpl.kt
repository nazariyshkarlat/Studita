package com.example.studita.data.repository

import com.example.studita.data.cache.chapter.ChapterCache
import com.example.studita.data.entity.mapper.ChapterDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.chapter.*
import com.example.studita.data.repository.datasource.exercises.DiskExercisesJsonDataStore
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreFactory
import com.example.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.repository.ChapterRepository

class ChapterRepositoryImpl(private val chapterJsonDataStoreFactory: ChapterJsonDataStoreFactory,
                            private val chapterDataMapper: ChapterDataMapper,
                            private val connectionManager: ConnectionManager): ChapterRepository {

    override suspend fun getChapter(chapterNumber: Int, offlineMode: Boolean): Pair<Int, ChapterData> =
        with(ChapterDataStoreImpl(chapterJsonDataStoreFactory.create(if(offlineMode || connectionManager.isNetworkAbsent()) ChapterJsonDataStoreFactory.Priority.CACHE else ChapterJsonDataStoreFactory.Priority.CLOUD)).getChapterEntity(chapterNumber)){
             this.first to chapterDataMapper.map(this.second)
        }

    override suspend fun downloadChapters(): Int {
        val diskDataStore = (chapterJsonDataStoreFactory.create(ChapterJsonDataStoreFactory.Priority.CACHE) as DiskChapterJsonDataStore)
        return if (!diskDataStore.chaptersAreCached()) {
            val json =
                (chapterJsonDataStoreFactory.create(ChapterJsonDataStoreFactory.Priority.CLOUD) as CloudChapterJsonDataStore).getAllChaptersJson()
            diskDataStore.saveChaptersJson(json)
            200
        } else
            409
    }

}