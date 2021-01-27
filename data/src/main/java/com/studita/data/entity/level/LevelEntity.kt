package com.studita.data.entity.level

import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import com.google.gson.annotations.SerializedName


data class LevelEntity(
    val levelNumber: Int,
    val levelName: String,
    val levelChildren: List<LevelChildEntity>
)

sealed class LevelChildEntity {
    data class LevelChapterEntity(
        @SerializedName("chapter_number") val chapterNumber: Int,
        @SerializedName("title") val chapterTitle: String,
        @SerializedName("subtitle") val chapterSubtitle: String,
        @SerializedName("chapter_parts_count") val chapterPartsCount: Int
    ) : LevelChildEntity()
}

fun LevelEntity.toBusinessEntity() = LevelData(
    levelNumber,
    levelName,
    levelChildren.map { it.toBusinessEntity() }
)

fun LevelChildEntity.toBusinessEntity() = when (this) {
    is LevelChildEntity.LevelChapterEntity -> LevelChildData.LevelChapterData(
        chapterNumber,
        chapterTitle,
        chapterSubtitle,
        chapterPartsCount
    )
}