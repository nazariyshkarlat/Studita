package com.example.studita.data.cache.chapter

interface ChapterCache{
    fun saveChapterJson(chapterNumber: Int, json: String)

    fun getLevelsJson(chapterNumber: Int): String?

    fun isCached(chapterNumber: Int): Boolean
}