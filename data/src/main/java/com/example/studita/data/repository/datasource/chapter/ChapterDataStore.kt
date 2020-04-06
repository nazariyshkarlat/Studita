package com.example.studita.data.repository.datasource.chapter

import com.example.studita.data.entity.ChapterEntity

interface ChapterDataStore{
    suspend fun getChapterEntity(chapterNumber: Int): Pair<Int, ChapterEntity>
}