package com.studita.data.entity.level

import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelEntity(
    @SerialName("level_number")val levelNumber: Int,
    @SerialName("level_name")val levelName: String,
    @SerialName("children")val levelChildren: List<LevelChildEntity>
)

@Serializable
sealed class LevelChildEntity {
    @Serializable
    @SerialName("chapter")
    data class LevelChapterEntity(
        @SerialName("chapter_number") val chapterNumber: Int,
        @SerialName("title") val chapterTitle: String,
        @SerialName("subtitle") val chapterSubtitle: String,
        @SerialName("chapter_parts_count") val chapterPartsCount: Int
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