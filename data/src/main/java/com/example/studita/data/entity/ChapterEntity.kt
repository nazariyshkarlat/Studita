package com.example.studita.data.entity

import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData
import com.google.gson.annotations.SerializedName

data class ChapterEntity(
    @SerializedName("chapter_number") val chapterNumber: Int,
    @SerializedName("title") val title: String,
    @SerializedName("parts") val parts: List<ChapterPartEntity>
)

data class ChapterPartEntity(
    @SerializedName("part_number") val number: Int,
    @SerializedName("name") val name: String
)

fun ChapterPartEntity.toBusinessEntity() = ChapterPartData(number, name)
fun ChapterEntity.toRawEntity() =
    ChapterData(chapterNumber, title, parts.map { it.toBusinessEntity() })