package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.level.LevelChildEntity
import com.example.studita.data.entity.level.LevelEntity
import com.example.studita.domain.entity.LevelChildData
import com.example.studita.domain.entity.LevelData

class LevelDataMapper : Mapper<List<LevelEntity>, List<LevelData>> {

    override fun map(source: List<LevelEntity>): List<LevelData> {
        return  source.map{ mapLevel(it) }
    }

    private fun mapLevel(source: LevelEntity): LevelData {
        return LevelData(
            source.levelNumber,
            mapChildren(source.levelChildren)
        )
    }

    private fun mapChildren(source: List<LevelChildEntity>) : List<LevelChildData>{
        return source.map { mapChild(it) }
    }

    private fun mapChild(source: LevelChildEntity): LevelChildData {
        return when(source) {
            is LevelChildEntity.LevelChapterEntity -> LevelChildData.LevelChapterData(source.chapterNumber, source.chapterTitle, source.chapterSubtitle, source.chapterPartsCount)
            is LevelChildEntity.LevelInterestingEntity -> LevelChildData.LevelInterestingData(source.interestingNumber, source.title, source.subtitle, source.tags)
        }
    }

}