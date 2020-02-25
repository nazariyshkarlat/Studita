package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.ChapterPartDataMapper
import com.example.studita.data.repository.datasource.chapter_parts.ChapterPartsDataStoreFactory
import com.example.studita.domain.entity.ChapterPartData
import com.example.studita.domain.repository.ChapterPartsRepository

class ChapterPartsRepositoryImpl(private val chapterPartsDataStoreFactory: ChapterPartsDataStoreFactory, private val chapterPartDataMapper: ChapterPartDataMapper ): ChapterPartsRepository{

    override suspend fun getChapterParts(chapterNumber: Int): Pair<Int, List<ChapterPartData>> =
        with(chapterPartsDataStoreFactory.create(ChapterPartsDataStoreFactory.Priority.CLOUD).getChapterPartsEntityList(chapterNumber)){
             this.first to chapterPartDataMapper.map(this.second)
        }

}