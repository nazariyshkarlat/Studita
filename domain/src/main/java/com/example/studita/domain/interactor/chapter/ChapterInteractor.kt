package com.example.studita.domain.interactor.chapter

import com.example.studita.domain.interactor.ChapterStatus

interface ChapterInteractor {

    suspend fun getChapter(chapterNumber: Int) : ChapterStatus

}