package com.example.studita.domain.interactor.chapter_parts

import com.example.studita.domain.interactor.ChapterPartsStatus

interface ChapterPartsInteractor {

    suspend fun getChapterParts(chapterNumber: Int) : ChapterPartsStatus

}