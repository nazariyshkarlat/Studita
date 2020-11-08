package com.studita.domain.repository

import com.studita.domain.entity.ChapterData


interface ChapterRepository {

    suspend fun getChapter(chapterNumber: Int, offlineMode: Boolean): Pair<Int, ChapterData>

    suspend fun downloadChapters(): Int

}
