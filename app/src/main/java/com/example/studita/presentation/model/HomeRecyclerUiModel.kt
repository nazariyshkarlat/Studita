package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

sealed class HomeRecyclerUiModel{
    data class HomeUserDataUiModel(val currentLevel: Int, val currentLevelXP: Int, val streakDays: Int): HomeRecyclerUiModel()
    data class LevelNumber(
        val levelNumber: Int
    ): HomeRecyclerUiModel()
    data class LevelChapterUiModel(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ): HomeRecyclerUiModel()
    data class LevelInterestingUiModel(
        val interestingNumber: Int,
        val title: String,
        val subtitle: String,
        val tags: List<String>
    ): HomeRecyclerUiModel()
}
