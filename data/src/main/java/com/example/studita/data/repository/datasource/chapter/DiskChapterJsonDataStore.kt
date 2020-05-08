package com.example.studita.data.repository.datasource.chapter

import com.example.studita.data.cache.chapter.ChapterCache
import com.example.studita.domain.exception.NetworkConnectionException

class DiskChapterJsonDataStore(
    private val chapterCache: ChapterCache
): ChapterJsonDataStore{
    override suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String> {
        val json = chapterCache.getLevelsJson(chapterNumber)
        return 200 to (json ?: throw NetworkConnectionException())
    }

    fun chaptersAreCached() = chapterCache.isCached(1)

    fun saveChaptersJson(json: String) = chapterCache.saveChaptersJson(json)


}