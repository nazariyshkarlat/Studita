package com.example.studita.data.repository.datasource.chapter

import com.example.studita.data.cache.chapter.ChapterCache
import com.example.studita.data.net.ChapterService
import com.example.studita.data.net.ChaptersService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

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
                val chapterAsync = chapterService.getChapterAsync(chapterNumber)
                val result = chapterAsync.await()
                return result.code() to result.body()!!.toString()
            } catch (e: Exception){
                throw ServerUnavailableException()
            }
        }
    }

    suspend fun getAllChaptersJson() : String{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val chapterAsync = chaptersService.getChaptersAsync()
                val result = chapterAsync.await()
                return result.body()!!.toString()
            }catch (e: Exception){
                throw ServerUnavailableException()
            }
        }
    }

}