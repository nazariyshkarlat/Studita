package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.ChapterEntity
import com.example.studita.data.entity.ChapterPartEntity
import com.example.studita.data.entity.level.LevelChildEntity
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.entity.LevelChildData

class ChapterDataMapper : Mapper<ChapterEntity, ChapterData>{
    override fun map(source: ChapterEntity): ChapterData
        = ChapterData(source.title, mapChapterParts(source.parts))

    private fun mapChapterParts(source: List<ChapterPartEntity>) : List<ChapterPartData>{
        return source.map { mapChapterPart(it) }
    }

    private fun mapChapterPart(source: ChapterPartEntity) = ChapterPartData(source.number, source.name)
}
