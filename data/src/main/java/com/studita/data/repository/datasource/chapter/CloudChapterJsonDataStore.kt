package com.studita.data.repository.datasource.chapter

import com.studita.data.net.ChapterService
import com.studita.data.net.ChaptersService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class CloudChapterJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val chapterService: ChapterService,
    private val chaptersService: ChaptersService
) : ChapterJsonDataStore {

    override suspend fun getChapterJson(chapterNumber: Int): Pair<Int, String> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val chapter = chapterService.getChapter(chapterNumber)
                return chapter.code() to chapter.body()!!.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    suspend fun getAllChaptersJson(): String {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val chapter = chaptersService.getChapters()
                return chapter.body()!!.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

}