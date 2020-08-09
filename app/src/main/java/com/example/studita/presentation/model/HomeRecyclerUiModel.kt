package com.example.studita.presentation.model

import com.example.studita.domain.entity.LevelChildData
import com.example.studita.domain.entity.LevelData

sealed class HomeRecyclerUiModel {
    object HomeUserDataUiModel : HomeRecyclerUiModel()
    data class HomeRecyclerLevelViewModel(
        val levelNumber: Int,
        val chaptersBounds: Pair<Int, Int>,
        val chapterPartsCount: Int
    ) : HomeRecyclerUiModel()

    data class LevelChapterUiModel(
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ) : HomeRecyclerUiModel()

    data class LevelInterestingUiModel(
        val interestingNumber: Int,
        val title: String,
        val subtitle: String,
        val tags: List<String>
    ) : HomeRecyclerUiModel()

    data class LevelSubscribeUiModel(
        val title: String,
        val button: List<String>
    ) : HomeRecyclerUiModel()
}

fun LevelData.toHomeRecyclerItems() = listOf(
    HomeRecyclerUiModel.HomeRecyclerLevelViewModel(levelNumber,
        levelChildren.filterIsInstance<LevelChildData.LevelChapterData>()
            .first().chapterNumber - 1 to levelChildren.filterIsInstance<LevelChildData.LevelChapterData>()
            .last().chapterNumber - 1,
        levelChildren.filterIsInstance<LevelChildData.LevelChapterData>()
            .map { it.chapterPartsCount }.sum()
    ), *levelChildren.map { it.toHomeRecyclerUiModel() }.toTypedArray()
)

fun LevelChildData.toHomeRecyclerUiModel() = when (this) {
    is LevelChildData.LevelChapterData -> HomeRecyclerUiModel.LevelChapterUiModel(
        chapterNumber,
        chapterTitle,
        chapterSubtitle,
        chapterPartsCount
    )
    is LevelChildData.LevelInterestingData -> HomeRecyclerUiModel.LevelInterestingUiModel(
        interestingNumber,
        title,
        subtitle,
        tags
    )
    is LevelChildData.LevelSubscribeData -> HomeRecyclerUiModel.LevelSubscribeUiModel(title, button)
}