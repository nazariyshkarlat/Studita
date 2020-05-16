package com.example.studita.data.repository.datasource.chapter

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
                val chapter = chapterService.getChapter(chapterNumber)
                return chapter.code() to chapter.body()!!.toString()
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
                val chapter = chaptersService.getChapters()
                return chapter.body()!!.toString()
            }catch (e: Exception){
                throw ServerUnavailableException()
            }
        }
    }

}