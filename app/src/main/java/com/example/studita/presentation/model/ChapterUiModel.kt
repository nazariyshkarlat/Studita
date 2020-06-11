package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData

data class ChapterUiModel(val chapterNumber: Int, val title: String, val parts: List<ChapterPartUiModel>)

data class ChapterPartUiModel( val chapterPartNumber: Int, val chapterPartName: String)

fun ChapterData.toUiModel() = ChapterUiModel(chapterNumber, title, parts.map { it.toUiModel() })
fun ChapterPartData.toUiModel() = ChapterPartUiModel(number, name)
