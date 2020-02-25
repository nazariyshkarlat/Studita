package com.example.studita.domain.repository

import com.example.studita.domain.entity.ChapterPartData


interface ChapterPartsRepository {

    suspend fun getChapterParts(chapterNumber: Int): Pair<Int, List<ChapterPartData>>

}
