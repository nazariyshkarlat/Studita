package com.example.studita.data.repository.datasource.chapter

import com.example.studita.data.database.chapter_parts.ChapterCache
import com.example.studita.data.net.ChapterService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class CloudChapterJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val chapterService: ChapterService,
    private val chapterCache: ChapterCache
) : ChapterJsonDataStore {

    override suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String> {
        val chapterAsync = chapterService.getChapterAsync(chapterNumber)
        println(chapterAsync)
        val result = chapterAsync.await()
        chapterCache.putLevelsJson(chapterNumber, result.body()!!.toString())
        return result.code() to result.body()!!.toString()
    }

}