package com.example.studita.presentation.model

import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData

data class ChapterUiModel(val chapterNumber: Int, val title: String, val parts: List<ChapterPartUiModel>)

data class ChapterPartUiModel(val chapterPartNumber: Int, val chapterPartName: String)

fun ChapterData.toShapeUiModel() = ChapterUiModel(chapterNumber, title, parts.map { it.toShapeUiModel() })
fun ChapterPartData.toShapeUiModel() = ChapterPartUiModel(number, name)
