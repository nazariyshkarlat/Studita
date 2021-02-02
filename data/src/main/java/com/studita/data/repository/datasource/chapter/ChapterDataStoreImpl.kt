package com.studita.data.repository.datasource.chapter

import com.studita.data.entity.ChapterEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChapterDataStoreImpl(private val chapterJsonDataStore: ChapterJsonDataStore) :
    ChapterDataStore {
    
    override suspend fun getChapterEntity(chapterNumber: Int) = chapterJsonDataStore.getChapterJson(chapterNumber).let{ it.first to Json.decodeFromString<ChapterEntity>(it.second)}

}