package com.studita.data.repository.datasource.chapter

interface ChapterJsonDataStore {

    suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String>

}