package com.example.studita.data.cache.chapter

interface ChaptersCache {
    fun saveChaptersJson(json: String)

    fun getChaptersJson(chapterNumber: Int): String?

    fun isCached(): Boolean

}