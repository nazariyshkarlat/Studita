package com.studita.domain.interactor.chapter

import com.studita.domain.interactor.ChapterStatus
import com.studita.domain.interactor.ChaptersCacheStatus

interface ChapterInteractor {

    suspend fun getChapter(
        chapterNumber: Int,
        offlineMode: Boolean,
        retryCount: Int = 3
    ): ChapterStatus

    suspend fun downloadChapters(retryCount: Int = 3): ChaptersCacheStatus

}