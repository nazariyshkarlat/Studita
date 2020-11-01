package com.example.studita.domain.entity

data class LevelData(
    val levelNumber: Int,
    val levelChildren: List<LevelChildData>
)

sealed class LevelChildData {
    data class LevelChapterData(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ) : LevelChildData()

    data class LevelInterestingData(
        val interestingNumber: Int,
        val title: String,
        val subtitle: String,
        val tags: List<String>
    ) : LevelChildData()

    data class LevelSubscribeData(
        val title: String,
        val button: List<String>,
        val isLoggedIn: Boolean
    ) : LevelChildData()
}