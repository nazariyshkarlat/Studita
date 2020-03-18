package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.LevelChildData
import com.example.studita.domain.entity.LevelData
import com.example.studita.presentation.model.LevelUiModel

class LevelUiModelMapper : Mapper<List<LevelData>, List<LevelUiModel>> {

    override fun map(source: List<LevelData>) =
        ArrayList<LevelUiModel>().apply {
            for(level in source) {
                add(
                    LevelUiModel.LevelNumber(
                        level.levelNumber
                    )
                )
                level.levelChildren.forEach {
                    add(mapChild(it))
                }
            }
        }


    private fun mapChild(source: LevelChildData): LevelUiModel {
        return when(source) {
            is LevelChildData.LevelChapterData -> LevelUiModel.LevelChapterUiModel(source.chapterNumber, source.chapterTitle, source.chapterSubtitle, source.tasksCount)
            is LevelChildData.LevelInterestingData -> LevelUiModel.LevelInterestingUiModel(source.interestingNumber, source.title, source.subtitle, source.tags)
        }
    }

}