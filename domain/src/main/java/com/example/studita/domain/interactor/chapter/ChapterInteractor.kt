package com.example.studita.domain.interactor.chapter

import com.example.studita.domain.interactor.ChapterStatus
import com.example.studita.domain.interactor.ChaptersCacheStatus

interface ChapterInteractor {

    suspend fun getChapter(chapterNumber: Int) : ChapterStatus

    suspend fun downloadChapters(): ChaptersCacheStatus

}