package com.studita.presentation.model

import com.studita.domain.entity.ChapterData
import com.studita.domain.entity.ChapterPartData

data class ChapterUiModel(
    val chapterNumber: Int,
    val title: String,
    val parts: List<ChapterPartUiModel>
)

data class ChapterPartUiModel(val chapterPartNumber: Int, val chapterPartName: String)

fun ChapterData.toChapterUiModel() =
    ChapterUiModel(chapterNumber, title, parts.map { it.toChapterPartUiModel() })

fun ChapterPartData.toChapterPartUiModel() = ChapterPartUiModel(number, name)
