package com.example.studita.presentation.model

import android.os.Parcel
import android.os.Parcelable

sealed class HomeRecyclerUiModel{
    object HomeUserDataUiModel : HomeRecyclerUiModel()
    data class HomeRecyclerLevelViewModel(
        val levelNumber: Int,
        val chaptersBounds: Pair<Int, Int>,
        val chapterPartsCount: Int
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
    data class LevelSubscribeUiModel(
        val title: String,
        val button: List<String>
    ): HomeRecyclerUiModel()
}
