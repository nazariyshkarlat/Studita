package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.ChapterPartEntity
import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.presentation.model.ChapterPartUiModel
import com.example.studita.presentation.model.ChapterUiModel

class ChapterUiModelMapper : Mapper<ChapterData, ChapterUiModel>{

    override fun map(source: ChapterData) = ChapterUiModel(source.title, mapChapterParts(source.parts))

    private fun mapChapterParts(source: List<ChapterPartData>) : List<ChapterPartUiModel>{
        return source.map { mapChapterPart(it) }
    }

    private fun mapChapterPart(source: ChapterPartData) = ChapterPartUiModel(source.number, source.name)

}