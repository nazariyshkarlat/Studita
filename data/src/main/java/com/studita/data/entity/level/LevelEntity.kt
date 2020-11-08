package com.studita.data.entity.level

import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import com.google.gson.annotations.SerializedName


data class LevelEntity(
    val levelNumber: Int,
    val levelChildren: List<LevelChildEntity>
)

sealed class LevelChildEntity {
    data class LevelChapterEntity(
        @SerializedName("chapter_number") val chapterNumber: Int,
        @SerializedName("title") val chapterTitle: String,
        @SerializedName("subtitle") val chapterSubtitle: String,
        @SerializedName("chapter_parts_count") val chapterPartsCount: Int
    ) : LevelChildEntity()

    data class LevelInterestingEntity(
        @SerializedName("interesting_number") val interestingNumber: Int,
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("tags") val tags: List<String>
    ) : LevelChildEntity()

    data class LevelSubscribeEntity(
        @SerializedName("title") val title: String,
        @SerializedName("button") val button: List<String>,
        @SerializedName("is_logged_in") val isLoggedIn: Boolean
    ) : LevelChildEntity()
}

fun LevelEntity.toBusinessEntity() = LevelData(
    levelNumber,
    levelChildren.map { it.toBusinessEntity() }
)

fun LevelChildEntity.toBusinessEntity() = when (this) {
    is LevelChildEntity.LevelChapterEntity -> LevelChildData.LevelChapterData(
        chapterNumber,
        chapterTitle,
        chapterSubtitle,
        chapterPartsCount
    )
    is LevelChildEntity.LevelInterestingEntity -> LevelChildData.LevelInterestingData(
        interestingNumber,
        title,
        subtitle,
        tags
    )
    is LevelChildEntity.LevelSubscribeEntity -> LevelChildData.LevelSubscribeData(title, button, isLoggedIn)
}