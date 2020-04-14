package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

data class ChapterUiModel(val chapterNumber: Int, val title: String, val parts: List<ChapterPartUiModel>)

data class ChapterPartUiModel( val chapterPartNumber: Int, val chapterPartName: String)