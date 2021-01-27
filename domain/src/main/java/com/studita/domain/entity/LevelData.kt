package com.studita.domain.entity

data class LevelData(
    val levelNumber: Int,
    val levelName: String,
    val levelChildren: List<LevelChildData>
)

sealed class LevelChildData {
    data class LevelChapterData(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ) : LevelChildData()

}