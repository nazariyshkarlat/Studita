package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.ChapterPartEntity
import com.example.studita.domain.entity.ChapterPartData

class ChapterPartDataMapper : Mapper<List<ChapterPartEntity>, List<ChapterPartData>>{
    override fun map(source: List<ChapterPartEntity>): List<ChapterPartData>
        = source.map{ChapterPartData(
            it.partNumber,
            it.partName
        )}
}