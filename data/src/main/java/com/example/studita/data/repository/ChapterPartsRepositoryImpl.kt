package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.ChapterDataMapper
import com.example.studita.data.repository.datasource.chapter_parts.ChapterPartsDataStoreFactory
import com.example.studita.domain.entity.ChapterData
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.repository.ChapterPartsRepository

class ChapterPartsRepositoryImpl(private val chapterPartsDataStoreFactory: ChapterPartsDataStoreFactory, private val chapterPartDataMapper: ChapterDataMapper): ChapterPartsRepository{

    override suspend fun getChapterParts(chapterNumber: Int): Pair<Int, ChapterData> =
        with(chapterPartsDataStoreFactory.create(ChapterPartsDataStoreFactory.Priority.CLOUD).getChapterPartsEntityList(chapterNumber)){
             this.first to chapterPartDataMapper.map(this.second)
        }

}