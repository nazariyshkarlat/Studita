package com.example.studita.domain.interactor.chapter_parts

import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.ChapterPartsStatus
import com.example.studita.domain.repository.ChapterPartsRepository
import java.lang.Exception

class ChapterPartsInteractorImpl(
    private val repository: ChapterPartsRepository
): ChapterPartsInteractor {

    override suspend fun getChapterParts(chapterNumber: Int): ChapterPartsStatus =
        try {
            val results = repository.getChapterParts(chapterNumber)

            if(results.first == 200)
                ChapterPartsStatus.Success(results.second)
            else
                ChapterPartsStatus.NoChapterFound

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                ChapterPartsStatus.NoConnection
            else
                ChapterPartsStatus.ServiceUnavailable
            }

}