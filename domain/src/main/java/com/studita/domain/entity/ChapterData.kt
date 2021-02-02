package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class ChapterData(val chapterNumber: Int, val title: String, val parts: List<ChapterPartData>, val exercisesCount: Int)

@Serializable
data class ChapterPartData(
    val number: Int,
    val name: String
)
