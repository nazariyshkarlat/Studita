package com.example.studita.data.entity.level

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
    ): LevelChildEntity()
    data class LevelInterestingEntity(
        @SerializedName("interesting_number") val interestingNumber: Int,
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("tags") val tags: List<String>
    ): LevelChildEntity()
    data class LevelSubscribeEntity(
        @SerializedName("title") val title: String,
        @SerializedName("button") val button: List<String>
    ): LevelChildEntity()
}