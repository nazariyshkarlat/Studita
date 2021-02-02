package com.studita.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelData(
    val levelNumber: Int,
    val levelName: String,
    val levelChildren: List<LevelChildData>
)

@Serializable
sealed class LevelChildData {

    @Serializable
    @SerialName("chapter")
    data class LevelChapterData(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ) : LevelChildData()

}