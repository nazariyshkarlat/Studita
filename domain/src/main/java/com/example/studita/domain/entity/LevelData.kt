package com.example.studita.domain.entity

data class LevelData(
    val levelNumber: Int,
    val levelChapters: List<LevelDataTask>
)

data class LevelDataTask(
    val chapterNumber: Int,
    val chapterTitle: String,
    val chapterSubtitle: String,
    val taskCount: Int
)