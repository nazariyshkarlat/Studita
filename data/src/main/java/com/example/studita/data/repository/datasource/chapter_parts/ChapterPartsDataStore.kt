package com.example.studita.data.repository.datasource.chapter_parts

import com.example.studita.data.entity.ChapterPartEntity

interface ChapterPartsDataStore {

    suspend fun getChapterPartsEntityList(chapterNumber: Int) : Pair<Int, List<ChapterPartEntity>>

}