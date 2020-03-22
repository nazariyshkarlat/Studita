package com.example.studita.domain.entity

data class ChapterData(val title : String, val parts: List<ChapterPartData>)

data class ChapterPartData(
    val number: Int,
    val name: String
)