package com.example.studita.data.repository.datasource.chapter

import com.example.studita.data.database.chapter_parts.ChapterCache
import com.example.studita.data.net.ChapterService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class CloudChapterJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val chapterService: ChapterService,
    private val chapterCache: ChapterCache
) : ChapterJsonDataStore {

    override suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val chapterAsync = chapterService.getChapterAsync(chapterNumber)
            val result = chapterAsync.await()
            chapterCache.saveChapterJson(chapterNumber, result.body()!!.toString())
            return result.code() to result.body()!!.toString()
        }
    }

}