package com.studita.data.repository.datasource.chapter

import com.studita.data.cache.chapter.ChaptersCache
import com.studita.domain.exception.NetworkConnectionException

class DiskChapterJsonDataStore(
    private val chaptersCache: ChaptersCache
) : ChapterJsonDataStore {
    override suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String> {
        val json = chaptersCache.getChaptersJson(chapterNumber)
        return 200 to (json ?: throw NetworkConnectionException())
    }

    fun chaptersAreCached() = chaptersCache.isCached()

    fun saveChaptersJson(json: String) = chaptersCache.saveChaptersJson(json)


}