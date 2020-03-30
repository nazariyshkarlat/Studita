package com.example.studita.data.database.chapter_parts

interface ChapterCache{
    fun putLevelsJson(chapterNumber: Int, json: String)

    fun getLevelsJson(chapterNumber: Int): String?

    fun isCached(chapterNumber: Int): Boolean
}