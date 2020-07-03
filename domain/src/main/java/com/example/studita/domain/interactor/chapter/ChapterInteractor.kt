package com.example.studita.domain.interactor.chapter

import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.interactor.ChaptersCacheStatus

interface ChapterInteractor {

    suspend fun getChapter(chapterNumber: Int, offlineMode: Boolean, retryCount: Int = 30) : ChapterStatus

    suspend fun downloadChapters(retryCount: Int = 30): ChaptersCacheStatus

}