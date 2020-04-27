package com.example.studita.data.cache.chapter

interface ChapterCache{
    fun saveChaptersJson(json: String)

    fun getLevelsJson(chapterNumber: Int): String?

    fun isCached(chapterNumber: Int): Boolean
}