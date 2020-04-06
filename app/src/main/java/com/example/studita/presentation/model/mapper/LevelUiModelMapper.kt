package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.LevelChildData
import com.example.studita.domain.entity.LevelData
import com.example.studita.presentation.model.HomeRecyclerUiModel

class LevelUiModelMapper : Mapper<List<LevelData>, List<HomeRecyclerUiModel>> {

    override fun map(source: List<LevelData>) =
        ArrayList<HomeRecyclerUiModel>().apply {
            for(level in source) {
                add(
                    HomeRecyclerUiModel.LevelNumber(
                        level.levelNumber
                    )
                )
                level.levelChildren.forEach {
                    add(mapChild(it))
                }
            }
        }


    private fun mapChild(source: LevelChildData): HomeRecyclerUiModel {
        return when(source) {
            is LevelChildData.LevelChapterData -> HomeRecyclerUiModel.LevelChapterUiModel(source.chapterNumber, source.chapterTitle, source.chapterSubtitle, source.chapterPartsCount)
            is LevelChildData.LevelInterestingData -> HomeRecyclerUiModel.LevelInterestingUiModel(source.interestingNumber, source.title, source.subtitle, source.tags)
        }
    }

}