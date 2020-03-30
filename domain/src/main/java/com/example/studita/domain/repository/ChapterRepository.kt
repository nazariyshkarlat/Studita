package com.example.studita.domain.repository

import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData


interface ChapterRepository {

    suspend fun getChapter(chapterNumber: Int): Pair<Int, ChapterData>

}
