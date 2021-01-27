package com.studita.presentation.model

import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import com.studita.utils.PrefsUtils

sealed class HomeRecyclerUiModel {
    object HomeUserDataUiModel : HomeRecyclerUiModel()
    data class HomeRecyclerLevelViewModel(
        val levelName: String,
        val levelNumber: Int,
        val chaptersCount: Int,
        var isExpanded: Boolean
    ) : HomeRecyclerUiModel()

    data class LevelChapterUiModel(
        val levelNumber: Int,
        val chapterNumber: Int,
        val chapterTitle: String,
        val chapterSubtitle: String,
        val chapterPartsCount: Int
    ) : HomeRecyclerUiModel()
}

@OptIn(ExperimentalStdlibApi::class)
fun LevelData.toHomeRecyclerItems(): List<HomeRecyclerUiModel> = PrefsUtils.getHomeLayoutCollapsedLevels().contains(levelNumber to true).let {
    buildList<HomeRecyclerUiModel> {
        add(HomeRecyclerUiModel.HomeRecyclerLevelViewModel(
            levelName,
            levelNumber,
            levelChildren.size,
            !it
        ))
        if(!it) addAll(levelChildren.map {child-> child.toHomeRecyclerUiModel(levelNumber) })
    }
}

fun LevelChildData.toHomeRecyclerUiModel(levelNumber: Int) = when (this) {
    is LevelChildData.LevelChapterData -> HomeRecyclerUiModel.LevelChapterUiModel(
        levelNumber,
        chapterNumber,
        chapterTitle,
        chapterSubtitle,
        chapterPartsCount
    )
}