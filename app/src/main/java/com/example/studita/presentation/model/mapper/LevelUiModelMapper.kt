package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
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
                for (levelChapter in level.levelChapters)
                    add(
                        LevelUiModel.LevelChapter(
                            levelChapter.chapterNumber,
                            levelChapter.chapterTitle,
                            levelChapter.chapterSubtitle,
                            levelChapter.taskCount
                        )
                    )
            }
        }

}