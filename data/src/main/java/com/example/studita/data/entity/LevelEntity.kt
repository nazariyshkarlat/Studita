package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class LevelEntity(
    @SerializedName("level_number") val levelNumber: Int,
    @SerializedName("level_chapters") val levelChapters: List<LevelChapterEntity>
)

data class LevelChapterEntity(
    @SerializedName("chapter_number") val chapterNumber: Int,
    @SerializedName("chapter_title") val chapterTitle: String,
    @SerializedName("chapter_subtitle") val chapterSubtitle: String,
    @SerializedName("tasks_count") val tasksCount: Int
)