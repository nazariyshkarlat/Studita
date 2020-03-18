package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

sealed class LevelUiModel{
    data class LevelNumber(
        val levelNumber: Int
    ): LevelUiModel()
    data class LevelChapterUiModel(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val tasksCount: Int
    ): LevelUiModel()
    data class LevelInterestingUiModel(
        val interestingNumber: Int,
        val title: String,
        val subtitle: String,
        val tags: List<String>
    ): LevelUiModel()
}
