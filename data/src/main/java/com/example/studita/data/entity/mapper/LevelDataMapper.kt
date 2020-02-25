package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.LevelChapterEntity
import com.example.studita.data.entity.LevelEntity
import com.example.studita.domain.entity.LevelData
import com.example.studita.domain.entity.LevelDataTask

class LevelDataMapper : Mapper<List<LevelEntity>, List<LevelData>> {

    override fun map(source: List<LevelEntity>): List<LevelData> {
        return  source.map{ mapLevel(it) }
    }

    private fun mapLevel(source: LevelEntity): LevelData {
        return LevelData(
            source.levelNumber,
            getLevelTasks(source.levelChapters)
        )
    }

    private fun getLevelTasks(source: List<LevelChapterEntity>) : List<LevelDataTask>{
        return source.map { mapTask(it) }
    }

    private fun mapTask(source: LevelChapterEntity): LevelDataTask {
        return LevelDataTask(
            source.chapterNumber,
            source.chapterTitle,
            source.chapterSubtitle,
            source.tasksCount
        )
    }

}