package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.presentation.model.ChapterPartUiModel

class ChapterPartUiModelMapper : Mapper<List<ChapterPartData>, List<ChapterPartUiModel>> {

    override fun map(source: List<ChapterPartData>) = source.map{
        ChapterPartUiModel(
            it.chapterPartNumber,
            it.chapterPartName
        )
    }

}