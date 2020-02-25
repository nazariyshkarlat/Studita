package com.example.studita.data.repository.datasource.chapter_parts

import com.example.studita.data.entity.ChapterPartEntity
import com.example.studita.data.net.ChapterPartsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class CloudChapterPartsDataStore(
    private val connectionManager: ConnectionManager,
    private val  chapterPartsService: ChapterPartsService
) : ChapterPartsDataStore {

    override suspend fun getChapterPartsEntityList(chapterNumber: Int): Pair<Int, List<ChapterPartEntity>> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val chapterParts: List<ChapterPartEntity>
            try {
                val chapterPartsAsync = chapterPartsService.getChapterPartsAsync(chapterNumber)
                val result = chapterPartsAsync.await()
                chapterParts = result.body()!!

                result.code() to chapterParts
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }
}