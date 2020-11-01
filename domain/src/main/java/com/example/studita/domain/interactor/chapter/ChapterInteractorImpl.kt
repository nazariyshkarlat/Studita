package com.example.studita.domain.interactor.chapter

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.interactor.ChaptersCacheStatus
import com.example.studita.domain.repository.ChapterRepository
import kotlinx.coroutines.delay

class ChapterInteractorImpl(
    private val repository: ChapterRepository
) : ChapterInteractor {

    private val retryDelay = 1000L

    override suspend fun getChapter(
        chapterNumber: Int,
        offlineMode: Boolean,
        retryCount: Int
    ): ChapterStatus =
        try {
            val results = repository.getChapter(chapterNumber, offlineMode)

            if (results.first == 200)
                ChapterStatus.Success(results.second)
            else
                ChapterStatus.NoChapterFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        ChapterStatus.NoConnection
                    else
                        ChapterStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getChapter(chapterNumber, offlineMode, retryCount - 1)
                }
            } else
                ChapterStatus.Failure
        }

    override suspend fun downloadChapters(retryCount: Int): ChaptersCacheStatus =
        try {
            val result = repository.downloadChapters()
            if (result == 200)
                ChaptersCacheStatus.Success
            else
                ChaptersCacheStatus.IsCached
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        ChaptersCacheStatus.NoConnection
                    else
                        ChaptersCacheStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    downloadChapters(retryCount - 1)
                }
            } else
                ChaptersCacheStatus.Failure
        }

}