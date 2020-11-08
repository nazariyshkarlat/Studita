package com.studita.domain.entity

data class ChapterData(val chapterNumber: Int, val title: String, val parts: List<ChapterPartData>, val exercisesCount: Int)

data class ChapterPartData(
    val number: Int,
    val name: String
)
