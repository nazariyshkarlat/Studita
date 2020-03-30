package com.example.studita.domain.interactor.chapter

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.repository.ChapterRepository
import java.lang.Exception

class ChapterInteractorImpl(
    private val repository: ChapterRepository
): ChapterInteractor {

    override suspend fun getChapter(chapterNumber: Int): ChapterStatus =
        try {
            val results = repository.getChapter(chapterNumber)

            if(results.first == 200)
                ChapterStatus.Success(results.second)
            else
                ChapterStatus.NoChapterFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                ChapterStatus.NoConnection
            else
                ChapterStatus.ServiceUnavailable
            }

}