package com.studita.data.repository.datasource.chapter

import com.studita.data.entity.ChapterEntity

interface ChapterDataStore {
    suspend fun getChapterEntity(chapterNumber: Int): Pair<Int, ChapterEntity>
}