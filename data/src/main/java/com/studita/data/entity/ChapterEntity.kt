package com.studita.data.entity

import com.studita.domain.entity.ChapterData
import com.studita.domain.entity.ChapterPartData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterEntity(
    @SerialName("chapter_number") val chapterNumber: Int,
    @SerialName("title") val title: String,
    @SerialName("parts") val parts: List<ChapterPartEntity>,
    @SerialName("exercises_count") val exercisesCount: Int
)

@Serializable
data class ChapterPartEntity(
    @SerialName("part_number") val number: Int,
    @SerialName("name") val name: String
)

fun ChapterPartEntity.toBusinessEntity() = ChapterPartData(number, name)
fun ChapterEntity.toRawEntity() =
    ChapterData(chapterNumber, title, parts.map { it.toBusinessEntity() }, exercisesCount)